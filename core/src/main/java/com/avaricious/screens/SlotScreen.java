package com.avaricious.screens;

import com.avaricious.Main;
import com.avaricious.RoundsManager;
import com.avaricious.slot.SlotMachine;
import com.avaricious.slot.Symbol;
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

import java.util.Arrays;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final SlotMachine slotMachine;
    private final RoundsManager roundsManager;

    private Long score = 0L;
    private final Vector3 mouse = new Vector3();
    private boolean wasPressed = false;

    private final GlyphLayout roundText;
    private final GlyphLayout scoreFormulaText;
    private final GlyphLayout symbolCounts;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final Rectangle spinButton;
    private final Rectangle applyButton;

    public SlotScreen(Main app) {
        this.app = app;
        slotMachine = new SlotMachine(app.getViewport().getWorldWidth(), app.getViewport().getWorldHeight());

        roundText = new GlyphLayout();
        scoreFormulaText = new GlyphLayout();
        symbolCounts = new GlyphLayout();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        font = generator.generateFont(parameter);
        generator.dispose();
        font.setUseIntegerPositions(false);
        font.getData().setScale(0.05f);

        shapeRenderer = new ShapeRenderer();
        spinButton = new Rectangle(0.5f, 2.5f, 0.5f, 0.5f);
        applyButton = new Rectangle(0.5f, 4f, 0.5f, 0.5f);

        roundsManager = RoundsManager.I();
    }

    @Override
    public void show() {
        roundsManager.nextRound();
        roundText.setText(font, "Round " + roundsManager.getCurrentRound() + ": Score " + roundsManager.getCurrentTargetScore() + " points");
        slotMachine.clearSelection();
        slotMachine.spin();
        updateSlotText();

        score = 0L;
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
        shapeRenderer.rect(applyButton.x, applyButton.y, applyButton.width, applyButton.height);
        shapeRenderer.rect(spinButton.x, spinButton.y, spinButton.width, spinButton.height);
        shapeRenderer.end();

        app.getBatch().begin();
        slotMachine.draw(app.getBatch());
        Rectangle slotBounds = slotMachine.getBounds();

        font.draw(app.getBatch(), roundText, (app.getViewport().getWorldWidth() - roundText.width) / 2f, slotBounds.y + slotBounds.height + 1.6f);
        font.draw(app.getBatch(), "Score: " + score, slotBounds.x, slotBounds.y + slotBounds.height + 0.8f);
        font.draw(app.getBatch(), scoreFormulaText, (app.getViewport().getWorldWidth() - scoreFormulaText.width) / 2f, slotBounds.y - 0.5f);
        font.draw(app.getBatch(), symbolCounts, slotBounds.x + slotBounds.width + 0.5f, symbolCounts.height);

        font.draw(app.getBatch(), roundsManager.getHandsLeft().toString(), applyButton.x + 0.75f, applyButton.y + applyButton.height);
        font.draw(app.getBatch(), roundsManager.getSpinsLeft().toString(), spinButton.x + 0.75f, spinButton.y + spinButton.height);
        app.getBatch().end();
    }

    private void handleInput() {
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        app.getViewport().unproject(mouse);

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
                    slotMachine.selectSymbolAt(col, row);
                    updateSlotText();
                }
            }
        }

        wasPressed = pressed;
    }

    private void onSpinButtonPressed() {
        if(roundsManager.getSpinsLeft() == 0) return;
        slotMachine.spin();
        roundsManager.minusOneSpin();
        updateSlotText();
    }

    private void onApplyButtonPressed() {
        score += slotMachine.applySelection();
        roundsManager.minusOneHand();
        updateSlotText();

        if(score >= roundsManager.getCurrentTargetScore()) {
            ScreenManager.I().setScreen(UpgradeSelectionScreen.class);
        } else if(roundsManager.getHandsLeft() == 0) {
            roundText.setText(font, "Haha! You lost (Score needed: " + roundsManager.getCurrentTargetScore() + ")");
        }
    }

    private void updateSlotText() {
        scoreFormulaText.setText(font, slotMachine.getScoreFormula());
        StringBuilder sb = new StringBuilder();
        Arrays.stream(Symbol.values()).forEach(symbol
            -> sb.append(symbol.toString(), 0, 2)
                .append(": ")
                .append(slotMachine.countSymbol(symbol))
                .append("\n"));
        symbolCounts.setText(font, sb.toString());
    }
}
