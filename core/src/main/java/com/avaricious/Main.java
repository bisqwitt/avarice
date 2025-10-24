package com.avaricious;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.List;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    FitViewport viewport;
    SpriteBatch slotsBatch;
    SlotGrid slotGrid;
    BetBar betBar;
    ShapeRenderer shapes;
    Rectangle spinButton;
    Vector3 mouse = new Vector3();
    boolean wasPressed = false;

    @Override
    public void create() {
        Assets.I().load();

        slotsBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        slotGrid = new SlotGrid(viewport.getWorldWidth(), viewport.getWorldHeight());

        betBar = new BetBar(0, 0.75f, viewport.getWorldWidth(), 1f);

        shapes = new ShapeRenderer();
        spinButton = new Rectangle(0.5f, 2.5f, 0.5f, 0.5f);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        betBar.handleInput(mouse.x, mouse.y);

        boolean pressed = Gdx.input.isButtonPressed(0);
        if(pressed && !wasPressed && spinButton.contains(mouse.x, mouse.y)) {
            onSpinButtonPressed();
        }
        wasPressed = pressed;
    }

    private void logic() {

    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        slotsBatch.setProjectionMatrix(viewport.getCamera().combined);

        slotsBatch.begin();
        slotGrid.draw(slotsBatch);
        slotsBatch.end();

        betBar.draw(slotsBatch, shapes);

        shapes.setProjectionMatrix(viewport.getCamera().combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.WHITE);
        shapes.rect(spinButton.x, spinButton.y, spinButton.width, spinButton.height);
        shapes.end();
    }

    private void onSpinButtonPressed() {
        slotGrid.randomizeAll();
        List<PatternHit> results = slotGrid.findPatternHits();

        Gdx.app.log("SPACER", "");
        if (!results.isEmpty()) results.forEach((pair)
            -> Gdx.app.log("MATCH", "Found " + pair.symbol().name() + " " + pair.type().toString()));
        else Gdx.app.log("MATCH", "No matches found");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
