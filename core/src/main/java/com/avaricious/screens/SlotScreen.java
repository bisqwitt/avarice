package com.avaricious.screens;

import com.avaricious.CreditManager;
import com.avaricious.Main;
import com.avaricious.RoundsManager;
import com.avaricious.slot.SlotMachine;
import com.avaricious.slot.Symbol;
import com.avaricious.slot.SymbolManager;
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
    private final GlyphLayout patternText;
    private final GlyphLayout symbolValueText;
    private final BitmapFont bigFont;
    private final BitmapFont smallFont;
    private final ShapeRenderer shapeRenderer;
    private final Rectangle spinButton;
    private final Rectangle applyButton;

    public SlotScreen(Main app) {
        this.app = app;

        roundText = new GlyphLayout();
        scoreFormulaText = new GlyphLayout();
        symbolValueText = new GlyphLayout();
        patternText = new GlyphLayout();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter bigSize = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bigSize.size = 16;
        FreeTypeFontGenerator.FreeTypeFontParameter smallSize = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallSize.size = 12;
        bigFont = generator.generateFont(bigSize);
        smallFont = generator.generateFont(smallSize);
        generator.dispose();
        bigFont.setUseIntegerPositions(false);
        bigFont.getData().setScale(0.05f);
        smallFont.setUseIntegerPositions(false);
        smallFont.getData().setScale(0.05f);

        shapeRenderer = new ShapeRenderer();
        spinButton = new Rectangle(0.5f, 2.5f, 0.5f, 0.5f);
        applyButton = new Rectangle(0.5f, 4f, 0.5f, 0.5f);

        slotMachine = new SlotMachine(app.getViewport().getWorldWidth(), app.getViewport().getWorldHeight());
        Arrays.stream(slotMachine.getGrid())
            .flatMap(Arrays::stream)
            .forEach(slot -> slot.setListener(finishedSlot -> updateSymbolText()));
        roundsManager = RoundsManager.I();
    }

    @Override
    public void show() {
        roundsManager.nextRound();
        roundText.setText(bigFont, "Round " + roundsManager.getCurrentRound() + ": Score " + roundsManager.getCurrentTargetScore() + " points");
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
        slotMachine.draw(app.getBatch(), delta);
        Rectangle slotBounds = slotMachine.getBounds();

        bigFont.draw(app.getBatch(), roundText, (app.getViewport().getWorldWidth() - roundText.width) / 2f, slotBounds.y + slotBounds.height + 1.6f);
        bigFont.draw(app.getBatch(), "Score: " + score, slotBounds.x, slotBounds.y + slotBounds.height + 0.8f);
        bigFont.draw(app.getBatch(), scoreFormulaText, (slotBounds.x / 2f) - scoreFormulaText.width / 2f, applyButton.y + 3);
        bigFont.draw(app.getBatch(), patternText, (app.getViewport().getWorldWidth() - patternText.width) / 2f, slotBounds.y - 0.5f);
        smallFont.draw(app.getBatch(), symbolValueText, slotBounds.x + slotBounds.width + 0.75f, 7f);

        bigFont.draw(app.getBatch(), roundsManager.getHandsLeft().toString(), applyButton.x + 0.75f, applyButton.y + applyButton.height);
        bigFont.draw(app.getBatch(), roundsManager.getSpinsLeft().toString(), spinButton.x + 0.75f, spinButton.y + spinButton.height);
        app.getBatch().end();
    }

    private void handleInput() {
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        app.getViewport().unproject(mouse);

        Rectangle slotBounds = slotMachine.getBounds();
        boolean pressed = Gdx.input.isButtonPressed(0);
        if(pressed && !wasPressed) {
            if(spinButton.contains(mouse.x, mouse.y)) onSpinButtonPressed();
            if(applyButton.contains(mouse.x, mouse.y)) onApplyButtonPressed();

            if(slotBounds.contains(mouse.x, mouse.y)) {
                int col = (int)((mouse.x - slotBounds.x) / (slotMachine.getCellW() + slotMachine.getSpacingX()));
                int row = (int)((mouse.y - slotBounds.y) / (slotMachine.getCellH() + slotMachine.getSpacingY()));

                if (col >= 0 && col < slotMachine.getCols() &&
                    row >= 0 && row < slotMachine.getRows()) {
                    slotMachine.selectSymbolAt(col, row);
                    updateSlotText();
                }
            }
        }
        slotMachine.hoveringAt(mouse);

        wasPressed = pressed;
    }

    private void onSpinButtonPressed() {
        if(roundsManager.getSpinsLeft() == 0) return;
        slotMachine.spin();
        roundsManager.minusOneSpin();
        updateSlotText();
    }

    private void onApplyButtonPressed() {
        if(roundsManager.getHandsLeft() == 0) return;
        score += slotMachine.applySelection();
        roundsManager.minusOneHand();
        updateSlotText();

        if(score >= roundsManager.getCurrentTargetScore()) {
            CreditManager.I().onRoundBeaten(roundsManager.getHandsLeft());
            ScreenManager.I().setScreen(UpgradeSelectionScreen.class);
        } else if(roundsManager.getHandsLeft() == 0) {
            roundText.setText(bigFont, "You lost (Score needed: " + roundsManager.getCurrentTargetScore() + ")");
        }
    }

    private void updateSlotText() {
        scoreFormulaText.setText(bigFont, slotMachine.getScoreFormula());
        patternText.setText(bigFont, slotMachine.getPatternText());
        updateSymbolText();
    }

    private void updateSymbolText() {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(Symbol.values()).forEach(symbol
            -> sb.append(symbol.toString(), 0, 2)
            .append(": ")
            .append(slotMachine.countSymbol(symbol))
            .append(" (")
            .append(SymbolManager.I().getSymbolValue(symbol))
            .append(")\n"));
        symbolValueText.setText(smallFont, sb.toString());
    }
}
