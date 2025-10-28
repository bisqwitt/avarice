package com.avaricious.screens;

import com.avaricious.Main;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeSelectionScreen extends ScreenAdapter {

    private final Main app;
    private final UpgradesManager upgradesManager;

    private final Vector3 mouse = new Vector3();
    private boolean wasPressed = false;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final Map<Rectangle, Upgrade> upgradeButtons = new HashMap<>();

    public UpgradeSelectionScreen(Main app) {
        this.app = app;
        upgradesManager = UpgradesManager.I();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        font = generator.generateFont(parameter);
        generator.dispose();
        font.setUseIntegerPositions(false);
        font.getData().setScale(0.05f);

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void show() {
        List<Upgrade> upgradeSelection = upgradesManager.randomUpgrades();
        upgradeSelection.forEach(upgrade
            -> upgradeButtons.put(new Rectangle(0.5f, 0.3f + (2f * upgradeSelection.indexOf(upgrade) + 1),
            0.5f, 0.5f),
            upgrade));
    }

    @Override
    public void render(float delta) {
        handleInput();

        ScreenUtils.clear(Color.BLACK);
        app.getViewport().apply();
        app.getBatch().setProjectionMatrix(app.getViewport().getCamera().combined);

        shapeRenderer.setProjectionMatrix(app.getViewport().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        upgradeButtons.keySet().forEach(button
            -> shapeRenderer.rect(button.x, button.y, button.width, button.height));
        shapeRenderer.end();

        app.getBatch().begin();
        upgradeButtons.forEach((button, upgrade)
            -> font.draw(app.getBatch(), upgrade.description(), button.x + 0.75f, button.y + button.height));
        app.getBatch().end();
    }

    private void handleInput() {
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        app.getViewport().unproject(mouse);

        boolean pressed = Gdx.input.isButtonPressed(0);
        if(pressed && !wasPressed) {
            upgradeButtons.forEach((button, upgrade) -> {
                if(button.contains(mouse.x, mouse.y)) onButtonPressed(upgrade);
            });
        }

        wasPressed = pressed;
    }

    private void onButtonPressed(Upgrade upgrade) {
        upgradesManager.applyUpgrade(upgrade);
        ScreenManager.I().setScreen(SlotScreen.class);
    }
}
