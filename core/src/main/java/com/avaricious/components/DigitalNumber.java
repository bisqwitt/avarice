package com.avaricious.components;

import com.avaricious.Assets;
import com.avaricious.Main;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class DigitalNumber {

    protected final List<Texture> numberTextures = new ArrayList<>();
    protected final List<Texture> numberShadowTextures = new ArrayList<>();
    protected final Color color;
    protected final Rectangle rectangle;
    protected final float offset;

    private int score;
    private int displayedScore;

    private boolean internalScoreIsDisplayed = true;

    private float hoverTime = 0f;

    private Runnable onInternalScoreDisplayed;

    public DigitalNumber(int initialScore, Color color, Rectangle rectangle, float offset) {
        setScore(initialScore);
        displayedScore = score;
        this.color = color;
        this.rectangle = rectangle;
        this.offset = offset;

        updateDigitalNumbers(initialScore);
    }

    public DigitalNumber(int initialScore, Color color, int setLength, Rectangle rectangle, float offset) {
        score = initialScore;
        this.color = color;
        this.rectangle = rectangle;
        this.offset = offset;

        for(int i = 0; i < setLength; i++) {
            numberTextures.add(Assets.I().getDigitalNumber(0));
            numberShadowTextures.add(Assets.I().getDigitalNumberShadow(0));
        }
    }

    public void draw(SpriteBatch batch, float delta) {
        if(displayedScore < score) {
            long diff = score - displayedScore;
            displayedScore += (int) Math.ceil(diff * 0.025);
            updateDigitalNumbers(displayedScore);
        } else if(displayedScore > score) {
            long diff = displayedScore - score;
            displayedScore -= (int) Math.ceil(diff * 0.025);
            updateDigitalNumbers(displayedScore);
        } else if(!internalScoreIsDisplayed) {
            internalScoreIsDisplayed = true;
            if(onInternalScoreDisplayed != null) onInternalScoreDisplayed.run();
        }

        hoverTime += delta;
        float numberBaseY = calcHoverY();

        batch.setColor(1f, 1f, 1f, 0.25f);
        for(int i = 0; i < numberTextures.size(); i++) {
            batch.draw(numberShadowTextures.get(i), rectangle.x + (i * offset) + 0.05f, numberBaseY - 0.05f, rectangle.width, rectangle.height);
        }
        batch.setColor(color);
        for(int i = 0; i < numberTextures.size(); i++) {
            batch.draw(numberTextures.get(i), rectangle.x + (i * offset), numberBaseY, rectangle.width, rectangle.height);
        }
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void updateDigitalNumbers(int score) {
        int tempScore = score;
        Assets assetManager = Assets.I();

        for(int i = numberTextures.size() -1; i >= 0; i--) {
            numberTextures.set(i, assetManager.getDigitalNumber(tempScore % 10));
            numberShadowTextures.set(i, assetManager.getDigitalNumberShadow(tempScore % 10));
            tempScore /= 10;
        }
    }

    public void setScore(int score) {
        this.score = score;
        int digits = score == 0 ? 1 : (int) Math.log10(score) + 1;
        while (digits > numberTextures.size()) {
            numberTextures.add(Assets.I().getDigitalNumber(0));
            numberShadowTextures.add(Assets.I().getDigitalNumberShadow(0));
        }
        internalScoreIsDisplayed = false;
    }

    protected float calcHoverY() {
        float hoverSpeed = 1.5f;
        float hoverStrength = 0.03f;
        float hoverOffset = (float) Math.sin(hoverTime * hoverSpeed) * hoverStrength;
        return rectangle.y + hoverOffset;
    }

    public int getScore() {
        return score;
    }

    public void setOnInternalScoreDisplayed(Runnable onInternalScoreDisplayed) {
        this.onInternalScoreDisplayed = onInternalScoreDisplayed;
    }
}
