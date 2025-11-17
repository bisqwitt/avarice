package com.avaricious.screens;

import com.avaricious.*;
import com.avaricious.slot.SlotMachine;
import com.avaricious.slot.Symbol;
import com.avaricious.slot.SymbolManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final SlotMachine slotMachine;
    private final ScoreDisplay scoreDisplay;
    private final UpgradeSticks upgradeSticks;
    private final RoundsManager roundsManager;
    private Long score = 0L;
    private Long displayedScore = 0L;
    private final Vector3 mouse = new Vector3();
    private boolean wasPressed = false;

    private final Texture slotMachineImg;
    private final Texture buttonBoard;
    private final Texture buttonsLeftDisplay;
    private TextureRegion applyButtonTexture;
    private TextureRegion spinButtonTexture;

    private final GlyphLayout roundText = new GlyphLayout();
    private final GlyphLayout scoreText = new GlyphLayout();
    private final GlyphLayout scoreFormulaText = new GlyphLayout();
    private final GlyphLayout patternText = new GlyphLayout();
    private final GlyphLayout symbolValueText = new GlyphLayout();
    private final List<TextureRegion> symbolValueIcons = new ArrayList<>();
    private final BitmapFont bigFont;
    private final ShapeRenderer shapeRenderer;
    private final Rectangle spinButton;
    private final Rectangle applyButton;

    private boolean hoveringApply = false;
    private boolean hoveringSpin = false;
    private boolean hoverApplyJustEntered = false;
    private boolean hoverSpinJustEntered = false;
    private float hoverAnimTime = 0f;


    public SlotScreen(Main app) {
        this.app = app;

        Arrays.stream(Symbol.values()).forEach(symbol -> symbolValueIcons.add(Assets.I().getBase(symbol)));
        Collections.reverse(symbolValueIcons);
        slotMachineImg = Assets.I().getSlotMachineBorder();
        buttonBoard = Assets.I().getButtonBoard();
        buttonsLeftDisplay = Assets.I().getButtonsLeftDisplay();
        applyButtonTexture = new TextureRegion(Assets.I().getApplyButton());
        spinButtonTexture = new TextureRegion(Assets.I().getSpinButton());

        bigFont = Assets.I().getBigFont();

        shapeRenderer = new ShapeRenderer();
        spinButton = new Rectangle(10.675f, 0.6f, 1f, 0.88f);
        applyButton = new Rectangle(8.05f, 0.6f, 2.15f, 0.95f);

        slotMachine = new SlotMachine(app.getViewport().getWorldWidth(), app.getViewport().getWorldHeight());
        upgradeSticks = new UpgradeSticks();
        scoreDisplay = new ScoreDisplay();
        roundsManager = RoundsManager.I();
    }

    @Override
    public void show() {
        roundsManager.nextRound();
        score = 0L;
        displayedScore = 0L;
        scoreDisplay.setScore(0);

        roundText.setText(bigFont, "Round " + roundsManager.getCurrentRound() + ": Score " + roundsManager.getCurrentTargetScore() + " points");
        scoreText.setText(bigFont, "" + displayedScore, Assets.I().lightColor(), 400f, Align.right, true);
        slotMachine.clearSelection();
        slotMachine.spin();
        updateSlotText();
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = app.getBatch();
        handleInput();

        ScreenUtils.clear(0.396f, 0.137f, 0.141f, 1f);
//        ScreenUtils.clear(0f, 0.349f, 0.204f, 1f)
        app.getViewport().apply();
        batch.setProjectionMatrix(app.getViewport().getCamera().combined);
        batch.begin();
        upgradeSticks.draw(batch);
        batch.draw(slotMachineImg, 5.15f, 2.1f, 9.6f, 6f);
        scoreDisplay.draw(batch);
        batch.draw(buttonsLeftDisplay, 0.75f, 3.75f, 3.84f, 2.6f);
        batch.draw(buttonBoard, 7.55f, 0.3f, 4.6f, 1.36f);
        //batch.draw(applyButtonTexture, 5.55f, 0.3f, 4.6f, 1.36f);
        drawApplyButton(batch, delta);
        drawSpinButton(batch, delta);
        batch.end();

//        shapeRenderer.setProjectionMatrix(app.getViewport().getCamera().combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.WHITE);
//        shapeRenderer.rect(spinButton.x, spinButton.y, spinButton.width, spinButton.height);
//        shapeRenderer.rect(potBounds.x, potBounds.y, potBounds.width, potBounds.height);
//        shapeRenderer.end();

        batch.begin();
        slotMachine.draw(app, delta);
        //batch.draw(Assets.I().getSlotMachineBorder(), 2.5f, 1.05f, 8.3f * 1.3f, 4.9f * 1.3f);
//        for(int i = 0; i < symbolValueIcons.size(); i++) {
//            batch.draw(symbolValueIcons.get(i), slotBounds.x + slotBounds.width + 1f, slotBounds.y - 0.25f + (i*0.75f), 0.75f, 0.75f);
//        }
        batch.end();

        if(displayedScore < score) {
            long diff = score - displayedScore;
            displayedScore += (long) Math.ceil(diff * 0.1);
            scoreDisplay.setScore(Math.toIntExact(displayedScore));
        }
        app.getUiViewport().apply();
        batch.setProjectionMatrix(app.getUiViewport().getCamera().combined);
        batch.begin();
//        bigFont.draw(batch, roundText, (app.getUiViewport().getWorldWidth() - roundText.width) / 2f, 800);
        scoreDisplay.draw(batch);
//        bigFont.draw(batch, scoreFormulaText, 135f - scoreFormulaText.width / 2f, 650f);
//        bigFont.draw(batch, patternText, (app.getUiViewport().getWorldWidth() - patternText.width) / 2f, 75f);
//        bigFont.draw(batch, symbolValueText, 1275f, 675f);
        app.getBatch().end();

        if(displayedScore >= roundsManager.getCurrentTargetScore()) {
            CreditManager.I().onRoundBeaten(roundsManager.getHandsLeft());
        } else if(roundsManager.getHandsLeft() == 0) {
            roundText.setText(bigFont, "You lost (Score needed: " + roundsManager.getCurrentTargetScore() + ")");
        }
    }

    private void handleInput() {
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        app.getViewport().unproject(mouse);

        Rectangle slotBounds = slotMachine.getBounds();
        boolean pressed = Gdx.input.isButtonPressed(0);

        boolean isHoveringNow = applyButton.contains(mouse.x, mouse.y);
        hoverApplyJustEntered = !hoveringApply && isHoveringNow; // mouse entered
        hoveringApply = isHoveringNow; // update state

        boolean isHoveringSpinNow = spinButton.contains(mouse.x, mouse.y);
        hoverSpinJustEntered = !hoveringSpin && isHoveringSpinNow;
        hoveringSpin = isHoveringSpinNow;

        if(pressed && !wasPressed) {
            if(applyButton.contains(mouse.x, mouse.y)) applyButtonTexture = new TextureRegion(Assets.I().getApplyButtonPressed());
            if(spinButton.contains(mouse.x, mouse.y)) spinButtonTexture = new TextureRegion(Assets.I().getSpinButtonPressed());

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

        if(!pressed && wasPressed) {
            applyButtonTexture = new TextureRegion(Assets.I().getApplyButton());
            spinButtonTexture = new TextureRegion(Assets.I().getSpinButton());
            if(applyButton.contains(mouse.x, mouse.y)) onApplyButtonPressed();
            if(spinButton.contains(mouse.x, mouse.y)) onSpinButtonPressed();
        }
        slotMachine.hoveringAt(mouse);
        upgradeSticks.hoveringAt(mouse);

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
        Arrays.stream(Symbol.values()).forEach(symbol
            -> sb.append(assetManager.colorBlue(SymbolManager.I().getSymbolValue(symbol)))
            .append("$\n"));
        symbolValueText.setText(bigFont, sb.toString());
    }

    private void drawApplyButton(SpriteBatch batch, float delta) {
        float x = 7.55f;
        float y = 0.3f;
        float w = 4.6f;
        float h = 1.36f;

// start animation on entry
        if (hoverApplyJustEntered) {
            hoverAnimTime = 0f;
        }

// play wiggle if animation active
        float scale = 1f;
        if (hoveringApply && hoverAnimTime < 0.25f) {
            hoverAnimTime += delta;

            // quick "pop" wiggle using sine ease-out
            float t = hoverAnimTime / 0.25f;       // 0 → 1
            float wiggle = (float) Math.sin(t * Math.PI * 3f) * (1f - t) * 0.01f;

            scale = 1f + wiggle;
        }

// draw with scaling from center
        float originX = w / 2f;
        float originY = h / 2f;

        batch.draw(applyButtonTexture,
            x + w/2f - originX * scale,
            y + h/2f - originY * scale,
            originX, originY,
            w, h,
            scale, scale,
            0);

    }

    private void drawSpinButton(SpriteBatch batch, float delta) {
        float x = 7.55f;
        float y = 0.3f;
        float w = 4.6f;
        float h = 1.36f;

// start animation on entry
        if (hoverSpinJustEntered) {
            hoverAnimTime = 0f;
        }

// play wiggle if animation active
        float scale = 1f;
        if (hoveringSpin && hoverAnimTime < 0.25f) {
            hoverAnimTime += delta;

            // quick "pop" wiggle using sine ease-out
            float t = hoverAnimTime / 0.25f;       // 0 → 1
            float wiggle = (float) Math.sin(t * Math.PI * 3f) * (1f - t) * 0.025f;

            scale = 1f + wiggle;
        }

// draw with scaling from center
        float originX = w / 2f;
        float originY = h / 2f;

        batch.draw(spinButtonTexture,
            x + w/2f - originX * scale,
            y + h/2f - originY * scale,
            originX, originY,
            w, h,
            scale, scale,
            0);

    }

}
