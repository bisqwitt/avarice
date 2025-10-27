package com.avaricious;

import com.avaricious.slot.SlotMachine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
    RoundsManager roundsManager;
    Long score = 0L;
    FitViewport viewport;
    SpriteBatch slotsBatch;
    Vector3 mouse = new Vector3();
    boolean wasPressed = false;

    GlyphLayout layout;
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

        layout = new GlyphLayout();
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

        roundsManager = RoundsManager.I();
        layout.setText(font, "Round: " + roundsManager.getCurrentRound() + " - Score " + roundsManager.getCurrentTargetScore() + " points");

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
        font.draw(slotsBatch, layout, (viewport.getWorldWidth() - layout.width) / 2f, slotBounds.y + slotBounds.height + 1.6f);
        font.draw(slotsBatch, "Score: " + score, slotBounds.x, slotBounds.y + slotBounds.height + 0.8f);
        font.draw(slotsBatch, slotMachine.getScoreFormula(), slotBounds.x, slotBounds.y - 0.5f);
        font.draw(slotsBatch, roundsManager.getHandsLeft().toString(), applyButton.x + 0.75f, applyButton.y + applyButton.height);
        font.draw(slotsBatch, roundsManager.getSpinsLeft().toString(), spinButton.x + 0.75f, spinButton.y + spinButton.height);
        slotsBatch.end();
    }

    private void onSpinButtonPressed() {
        if(roundsManager.getSpinsLeft() == 0) return;
        slotMachine.spin();
        roundsManager.minusOneSpin();
    }

    private void onApplyButtonPressed() {
        score += slotMachine.applySelection();
        roundsManager.minusOneHand();

        if(score >= roundsManager.getCurrentTargetScore()) {
            roundsManager.nextRound();
            layout.setText(font, "Round: " + roundsManager.getCurrentRound() + " - Score " + roundsManager.getCurrentTargetScore() + " points");
            slotMachine.clearSelection();
            slotMachine.spin();

            score = 0L;
        } else if(roundsManager.getHandsLeft() == 0) {
            layout.setText(font, "Haha! You lost (Score needed: " + roundsManager.getCurrentTargetScore() + ")");
        }
    }

    private Integer handsPerRound() {
        return 4;
    }

    private Integer spinsPerRound() {
        return 4;
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
