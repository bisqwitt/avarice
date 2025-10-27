package com.avaricious;

import com.avaricious.slot.SlotMachine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    SlotMachine slotMachine;
    Long score = 0L;
    Integer hands = 5;
    Integer spins = 5;

    FitViewport viewport;
    SpriteBatch slotsBatch;
    Vector3 mouse = new Vector3();
    boolean wasPressed = false;

    BitmapFont font;
    ShapeRenderer shapes;
    Rectangle spinButton;
    Rectangle applyButton;

    @Override
    public void create() {
        Assets.I().load();

        slotsBatch = new SpriteBatch();
        viewport = new FitViewport(16, 9);
        slotMachine = new SlotMachine(viewport.getWorldWidth(), viewport.getWorldHeight());

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        font = generator.generateFont(parameter);
        generator.dispose();
        font.setUseIntegerPositions(false);
        font.getData().setScale(0.05f);

        shapes = new ShapeRenderer();
        spinButton = new Rectangle(0.5f, 2.5f, 0.5f, 0.5f);
        applyButton = new Rectangle(0.5f, 4f, 0.5f, 0.5f);
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

        boolean pressed = Gdx.input.isButtonPressed(0);
        if(pressed && !wasPressed) {
            if(spinButton.contains(mouse.x, mouse.y)) onSpinButtonPressed();
            if(applyButton.contains(mouse.x, mouse.y)) onApplyButtonPressed();

            Rectangle slotBounds = slotMachine.getBounds();
            if(slotBounds.contains(mouse.x, mouse.y)) {
                int col = (int)((mouse.x - slotBounds.x) / slotMachine.getCellW());
                int row = (int)((mouse.y - slotBounds.y) / slotMachine.getCellH());

                if (col >= 0 && col < slotMachine.getCols() &&
                    row >= 0 && row < slotMachine.getRows()) {
                    // select the symbol type
                    slotMachine.selectSymbolAt(col, row);
                }
            }
        }

        wasPressed = pressed;
    }

    private void logic() {

    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        slotsBatch.setProjectionMatrix(viewport.getCamera().combined);

        shapes.setProjectionMatrix(viewport.getCamera().combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.WHITE);
        shapes.rect(applyButton.x, applyButton.y, applyButton.width, applyButton.height);
        shapes.rect(spinButton.x, spinButton.y, spinButton.width, spinButton.height);
        shapes.end();

        slotsBatch.begin();
        slotMachine.draw(slotsBatch);
        Rectangle slotBounds = slotMachine.getBounds();
        font.draw(slotsBatch, "Score: " + score, slotBounds.x, slotBounds.y + slotBounds.height + 1f);
        font.draw(slotsBatch, slotMachine.getScoreFormula(), slotBounds.x, slotBounds.y - 0.5f);
        font.draw(slotsBatch, hands.toString(), applyButton.x + 0.75f, applyButton.y + applyButton.height);
        font.draw(slotsBatch, spins.toString(), spinButton.x + 0.75f, spinButton.y + spinButton.height);
        slotsBatch.end();
    }

    private void onSpinButtonPressed() {
        if(spins == 0) return;
        slotMachine.spin();
        spins--;
    }

    private void onApplyButtonPressed() {
        if(hands == 0) return;
        score += slotMachine.applySelection();
        hands--;
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
