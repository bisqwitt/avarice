package com.avaricious.screens;

import com.avaricious.CreditManager;
import com.avaricious.Main;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
    private final CreditManager creditManager;

    private final Vector3 mouse = new Vector3();
    private boolean wasPressed = false;
    private final BitmapFont font;
    private final GlyphLayout creditText;
    private final ShapeRenderer shapeRenderer;
    private final Rectangle nextRoundButton;
    private final Rectangle rerollShopButton;
    private final Map<Rectangle, Upgrade> buyUpgradeButtons = new HashMap<>();
    private final Map<Rectangle, Upgrade> sellUpgradeButtons = new HashMap<>();

    public UpgradeSelectionScreen(Main app) {
        this.app = app;
        upgradesManager = UpgradesManager.I();
        creditManager = CreditManager.I();

        creditText = new GlyphLayout();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelifySans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        font = generator.generateFont(parameter);
        generator.dispose();
        font.setUseIntegerPositions(false);
        font.getData().setScale(0.05f);

        shapeRenderer = new ShapeRenderer();
        nextRoundButton = new Rectangle(14.5f, 0.5f, 0.5f, 0.5f);
        rerollShopButton = new Rectangle(14.5f, 6f, 0.5f, 0.5f);
    }

    @Override
    public void show() {
        updateCreditText();
        setupShopItems();
        updateDeck();
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
        shapeRenderer.rect(nextRoundButton.x, nextRoundButton.y, nextRoundButton.width, nextRoundButton.height);
        shapeRenderer.rect(rerollShopButton.x, rerollShopButton.y, rerollShopButton.width, rerollShopButton.height);
        buyUpgradeButtons.keySet().forEach(button
            -> shapeRenderer.rect(button.x, button.y, button.width, button.height));
        sellUpgradeButtons.keySet().forEach(button
            -> shapeRenderer.rect(button.x, button.y, button.width, button.height));
        shapeRenderer.rectLine(0.5f, 5.75f, 14f, 5.75f, 0.05f);
        shapeRenderer.end();

        app.getBatch().begin();
        font.draw(app.getBatch(), creditText, 14.5f, 8.5f);
        buyUpgradeButtons.forEach((button, upgrade)
            -> font.draw(app.getBatch(), upgrade.description(), button.x + 0.75f, button.y + button.height));
        sellUpgradeButtons.forEach((button, upgrade)
            -> font.draw(app.getBatch(), upgrade.description(), button.x + 0.75f, button.y + button.height));
        app.getBatch().end();
    }

    private void handleInput() {
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        app.getViewport().unproject(mouse);

        boolean pressed = Gdx.input.isButtonPressed(0);
        if(pressed && !wasPressed) {
            if(nextRoundButton.contains(mouse.x, mouse.y)) onNextRoundButtonPressed();
            if(rerollShopButton.contains(mouse.x, mouse.y)) onRerollShopButtonPressed();

            Map.copyOf(buyUpgradeButtons).forEach((button, upgrade) -> {
                if(button.contains(mouse.x, mouse.y)) onBuyUpgradeButtonPressed(button, upgrade);
            });
            Map.copyOf(sellUpgradeButtons).forEach((button, upgrade) -> {
                if(button.contains(mouse.x, mouse.y)) onSellUpgradeButtonPressed(upgrade);
            });
        }

        wasPressed = pressed;
    }

    private void setupShopItems() {
        List<? extends Upgrade> upgradeSelection = upgradesManager.randomUpgrades();
        upgradeSelection.forEach(upgrade
            -> buyUpgradeButtons.put(new Rectangle(0.5f, 5f + (1f * upgradeSelection.indexOf(upgrade) + 1),
                0.5f, 0.5f),
            upgrade));
    }

    private void updateDeck() {
        sellUpgradeButtons.clear();
        List<? extends Upgrade> deck = upgradesManager.getDeck();
        deck.forEach(upgrade
            -> sellUpgradeButtons.put(new Rectangle(0.5f, (1f * deck.indexOf(upgrade) + 0.5f),
                0.5f, 0.5f),
            upgrade));
    }

    public void onNextRoundButtonPressed() {
        ScreenManager.I().setScreen(SlotScreen.class);
    }

    public void onRerollShopButtonPressed() {
        if(creditManager.payIfEnough(1)) {
            buyUpgradeButtons.clear();
            updateCreditText();
            setupShopItems();
        }
    }

    private void onBuyUpgradeButtonPressed(Rectangle button, Upgrade upgrade) {
        if(upgradesManager.spaceInDeck() && creditManager.payIfEnough(upgrade.getPrice())) {
            buyUpgradeButtons.remove(button);
            upgradesManager.addUpgrade(upgrade);
            updateCreditText();
            updateDeck();
        }
    }

    private void onSellUpgradeButtonPressed(Upgrade upgrade) {
        creditManager.gain(upgrade.getPrice());
        upgradesManager.removeUpgrade(upgrade);
        updateCreditText();
        updateDeck();
    }

    private void updateCreditText() {
        creditText.setText(font, creditManager.getCredits() + "$");
    }
}
