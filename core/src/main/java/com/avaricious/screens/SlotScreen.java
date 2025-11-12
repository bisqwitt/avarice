package com.avaricious.screens;

import com.avaricious.Assets;
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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final SlotMachine slotMachine;
    private final RoundsManager roundsManager;
    private Long score = 0L;
    private Long displayedScore = 0L;
    private final Vector3 mouse = new Vector3();
    private boolean wasPressed = false;

    private final GlyphLayout roundText = new GlyphLayout();
    private final GlyphLayout scoreText = new GlyphLayout();
    private final GlyphLayout scoreFormulaText = new GlyphLayout();
    private final GlyphLayout patternText = new GlyphLayout();
    private final GlyphLayout symbolCountText = new GlyphLayout();
    private final List<TextureRegion> symbolValueIcons = new ArrayList<>();
    private final BitmapFont bigFont;
    private final BitmapFont smallFont;
    private final ShapeRenderer shapeRenderer;
    private final Rectangle spinButton;
    private final Rectangle applyButton;

    public SlotScreen(Main app) {
        this.app = app;

        Arrays.stream(Symbol.values()).forEach(symbol -> symbolValueIcons.add(Assets.I().getBase(symbol)));
        Collections.reverse(symbolValueIcons);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelifySans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter bigSize = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bigSize.size = 56;
        FreeTypeFontGenerator.FreeTypeFontParameter smallSize = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallSize.size = 46;
        bigFont = generator.generateFont(bigSize);
        smallFont = generator.generateFont(smallSize);
        generator.dispose();
        bigFont.setUseIntegerPositions(false);
        bigFont.getData().markupEnabled = true;
        smallFont.setUseIntegerPositions(false);
        smallFont.getData().markupEnabled = true;

        shapeRenderer = new ShapeRenderer();
        spinButton = new Rectangle(0.5f, 2.5f, 0.5f, 0.5f);
        applyButton = new Rectangle(0.5f, 4f, 0.5f, 0.5f);

        slotMachine = new SlotMachine(app.getViewport().getWorldWidth(), app.getViewport().getWorldHeight());
        roundsManager = RoundsManager.I();
    }

    @Override
    public void show() {
        roundsManager.nextRound();
        score = 0L;
        displayedScore = 0L;

        roundText.setText(bigFont, "Round " + roundsManager.getCurrentRound() + ": Score " + roundsManager.getCurrentTargetScore() + " points");
        scoreText.setText(bigFont, "Score: " + displayedScore);
        slotMachine.clearSelection();
        slotMachine.spin();
        updateSlotText();
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = app.getBatch();
        handleInput();

        ScreenUtils.clear(Color.BLACK);
        app.getViewport().apply();
        batch.setProjectionMatrix(app.getViewport().getCamera().combined);

        shapeRenderer.setProjectionMatrix(app.getViewport().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(applyButton.x, applyButton.y, applyButton.width, applyButton.height);
        shapeRenderer.rect(spinButton.x, spinButton.y, spinButton.width, spinButton.height);
        shapeRenderer.end();

        batch.begin();
        slotMachine.draw(app, delta);
        Rectangle slotBounds = slotMachine.getBounds();
        //batch.draw(Assets.I().getSlotMachineBorder(), 2.5f, 1.05f, 8.3f * 1.3f, 4.9f * 1.3f);
        float size = 0.6f;
        for(int i = 0; i < symbolValueIcons.size(); i++) {
            batch.draw(symbolValueIcons.get(i), slotBounds.x + slotBounds.width + 0.25f, (slotBounds.y + (i*size)) + 0.1f, size, size);
        }
        batch.end();

        shapeRenderer.setProjectionMatrix(app.getViewport().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(slotBounds.x, slotBounds.y, slotBounds.width, slotBounds.height);
        shapeRenderer.rect(slotBounds.x + slotBounds.width, slotBounds.y, 1.75f, 4.35f);
        shapeRenderer.end();

        if(displayedScore < score) {
            long diff = score - displayedScore;
            displayedScore += (long) Math.ceil(diff * 0.1);

            scoreText.setText(bigFont, "Score: " + displayedScore);
        }
        app.getUiViewport().apply();
        batch.setProjectionMatrix(app.getUiViewport().getCamera().combined);
        batch.begin();
        bigFont.draw(batch, roundText, (app.getUiViewport().getWorldWidth() - roundText.width) / 2f, 775);
        bigFont.draw(batch, scoreText, (app.getUiViewport().getWorldWidth() - scoreText.width) / 2f, 725);
        bigFont.draw(batch, scoreFormulaText, 135f - scoreFormulaText.width / 2f, 600f);
        bigFont.draw(batch, patternText, (app.getUiViewport().getWorldWidth() - patternText.width) / 2f, 75f);
        smallFont.draw(batch, symbolCountText, 1140f, 590f);

        smallFont.draw(batch, "Select: " + roundsManager.getHandsLeft().toString(), 45f, 350f);
        smallFont.draw(batch, "Spin: " + roundsManager.getSpinsLeft().toString(), 45f, 215f);
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
//        score += slotMachine.applySelection();
        slotMachine.applySelection();
        roundsManager.minusOneHand();
        updateSlotText();

        if(score >= roundsManager.getCurrentTargetScore()) {
            CreditManager.I().onRoundBeaten(roundsManager.getHandsLeft());
            ScreenManager.I().setScreen(UpgradeSelectionScreen.class);
        } else if(roundsManager.getHandsLeft() == 0) {
            roundText.setText(bigFont, "You lost (Score needed: " + roundsManager.getCurrentTargetScore() + ")");
        }
    }

    public void updateSlotText() {
        Assets assetManager = Assets.I();

        String[] parts = slotMachine.getScoreFormula().split(" x ");
        scoreFormulaText.setText(bigFont, parts.length > 1 ? assetManager.colorBlue(parts[0])
            + " x " + assetManager.colorRed(parts[1]) : "");

        String pattern = slotMachine.getPatternText();
        int splitIndex = pattern.indexOf(" of a kind");
        patternText.setText(bigFont, splitIndex == -1 ? "" : assetManager.colorRed(pattern.substring(0, splitIndex)) + pattern.substring(splitIndex));

        StringBuilder sb = new StringBuilder();
        slotMachine.getPicks().values().forEach(count -> sb.append(count).append("\n"));
        symbolCountText.setText(smallFont, sb.toString());
    }

}
