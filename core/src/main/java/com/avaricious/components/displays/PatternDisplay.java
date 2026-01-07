package com.avaricious.components.displays;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PatternDisplay {

    private final Texture multSymbolTexture;
    private final Texture multSymbolShadow;

    private final TextureRegion[] pointDigitalNumbers = new TextureRegion[3];
    private final TextureRegion[] pointNumberShadows = new TextureRegion[3];
    private final TextureRegion[] multiDigitalNumbers = new TextureRegion[3];
    private final TextureRegion[] multiNumberShadows = new TextureRegion[3];
    private final TextureRegion[] streakDigitalNumbers = new TextureRegion[2];
    private final TextureRegion[] streakNumberShadows = new TextureRegion[2];

    private float pointsValue = 0L;
    private float multiValue = 0L;
    private float streakValue = 0L;

    private float pointsPulseTime = 0f;
    private float multiPulseTime  = 0f;
    private float streakPulseTime = 0f;

    private float hoverTime = 0f;

    public PatternDisplay() {
        multSymbolTexture = Assets.I().mult();
        multSymbolShadow = Assets.I().multShadow();
        for(int i = 0; i < 3; i++) {
            pointDigitalNumbers[i] = new TextureRegion(Assets.I().getDigitalNumber(0));
            pointNumberShadows[i] = new TextureRegion(Assets.I().getDigitalNumberShadow(0));
            multiDigitalNumbers[i] = new TextureRegion(Assets.I().getDigitalNumber(0));
            multiNumberShadows[i] = new TextureRegion(Assets.I().getDigitalNumberShadow(0));
        }
        streakDigitalNumbers[0] = new TextureRegion(Assets.I().getDigitalNumber(0));
        streakNumberShadows[0] = new TextureRegion(Assets.I().getDigitalNumberShadow(0));
        streakDigitalNumbers[1] = new TextureRegion(Assets.I().getDigitalNumber(0));
        streakNumberShadows[1] = new TextureRegion(Assets.I().getDigitalNumberShadow(0));
    }


    public void draw(SpriteBatch batch, float delta) {
        hoverTime += delta;
        float hoverOffset = (float) Math.sin(hoverTime * 1.5f) * 0.03f;
        float baseY = 5.025f;
        float numberBaseY = 5.675f + hoverOffset;

        // ----- Pulse + wobble for POINTS -----
        float pointsScale = 1f;
        float pointsRotation = 0f;
        // seconds for the pulse
        float pulseDuration = 0.2f;
        if (pointsPulseTime < pulseDuration) {
            pointsPulseTime += delta;
            float t = pointsPulseTime / pulseDuration;
            if (t > 1f) t = 1f;

            // Parabola: 0 -> 1 -> 0 (single bump)
            float pulseCurve = 1f - 4f * (t - 0.5f) * (t - 0.5f);
            if (pulseCurve < 0f) pulseCurve = 0f;

            float baseScale = 1.0f;
            float pulseScale = 0.35f;  // same feel as NumberPopup
            pointsScale = baseScale + pulseCurve * pulseScale;

            float wobbleAngle = 8f;    // degrees
            pointsRotation = pulseCurve * wobbleAngle;
        }

        // ----- Pulse + wobble for MULTI -----
        float multiScale = 1f;
        float multiRotation = 0f;
        if (multiPulseTime < pulseDuration) {
            multiPulseTime += delta;
            float t = multiPulseTime / pulseDuration;
            if (t > 1f) t = 1f;

            float pulseCurve = 1f - 4f * (t - 0.5f) * (t - 0.5f);
            if (pulseCurve < 0f) pulseCurve = 0f;

            float baseScale = 1.0f;
            float pulseScale = 0.35f;
            multiScale = baseScale + pulseCurve * pulseScale;

            float wobbleAngle = 8f;
            multiRotation = pulseCurve * wobbleAngle;
        }

        // ----- Pulse + wobble for X MULTI -----
        float streakScale = 1f;
        float streakRotation = 0f;
        if (streakPulseTime < pulseDuration) {
            streakPulseTime += delta;
            float t = streakPulseTime / pulseDuration;
            if (t > 1f) t = 1f;

            float pulseCurve = 1f - 4f * (t - 0.5f) * (t - 0.5f);
            if (pulseCurve < 0f) pulseCurve = 0f;

            float baseScale = 1.0f;
            float pulseScale = 0.35f;
            streakScale = baseScale + pulseCurve * pulseScale;

            float wobbleAngle = 8f;
            streakRotation = pulseCurve * wobbleAngle;
        }

        // Common digit size/origin
        float digitWidth  = 8 / 25f;
        float digitHeight = 14 / 25f;
        float originX = digitWidth / 2f;
        float originY = digitHeight / 2f;

        // Draw POINTS (blue)
        for (int i = 0; i < 3; i++) {
            float x = 1.57f + (i * 0.35f);
            batch.setColor(1f, 1f, 1f, 0.25f);
            batch.draw(
                pointNumberShadows[i],
                x - originX + 0.05f, numberBaseY - originY - 0.05f,
                originX, originY,
                digitWidth, digitHeight,
                pointsScale, pointsScale,
                pointsRotation
            );
            batch.setColor(Assets.I().colorBlue());
            batch.draw(
                pointDigitalNumbers[i],
                x - originX,           // x (bottom-left with origin in center)
                numberBaseY - originY, // y
                originX,               // originX
                originY,               // originY
                digitWidth,
                digitHeight,
                pointsScale,
                pointsScale,
                pointsRotation
            );
        }

        // Draw MULTI (red)
        for (int i = 0; i < 3; i++) {
            float x = 3.17f + (i * 0.35f);
            batch.setColor(1f, 1f, 1f, 0.25f);
            batch.draw(
                multiNumberShadows[i],
                x - originX + 0.05f, numberBaseY - originY - 0.05f,
                originX, originY,
                digitWidth, digitHeight,
                multiScale, multiScale,
                multiRotation
            );
            batch.setColor(Assets.I().colorRed());
            batch.draw(
                multiDigitalNumbers[i],
                x - originX,
                numberBaseY - originY,
                originX,
                originY,
                digitWidth,
                digitHeight,
                multiScale,
                multiScale,
                multiRotation
            );
        }

        // Draw X MULTI (red)
        for (int i = 0; i < 2; i++) {
            float x = 4.77f + (i * 0.35f);
            batch.setColor(1f, 1f, 1f, 0.25f);
            batch.draw(
                streakNumberShadows[i],
                x - originX + 0.05f, numberBaseY - originY - 0.05f,
                originX, originY,
                digitWidth, digitHeight,
                streakScale,
                streakScale,
                streakRotation
            );
            batch.setColor(Assets.I().colorRed());
            batch.draw(
                streakDigitalNumbers[i],
                x - originX,
                numberBaseY - originY,
                originX,
                originY,
                digitWidth,
                digitHeight,
                streakScale,
                streakScale,
                streakRotation
            );
        }
        batch.setColor(1f, 1f, 1f, 0.25f);
        batch.draw(multSymbolShadow, 2.61f, numberBaseY - originY - 0.05f, 0.35f, 0.35f);
        batch.draw(multSymbolShadow, 4.21f, numberBaseY - originY - 0.05f, 0.35f, 0.35f);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(multSymbolTexture, 2.56f, numberBaseY - originY, 0.35f, 0.35f);
        batch.draw(multSymbolTexture, 4.16f, numberBaseY - originY, 0.35f, 0.35f);
    }

    public void resetBaseValues() {
        pointsPulseTime = 0f;
        multiPulseTime  = 0f;

        pointsValue = 0f;
        multiValue = 0f;

        updateDisplayedPoints();
        updateDisplayedMulti();
    }

    public void reset() {
        resetBaseValues();

        streakPulseTime = 0f;
        streakValue = 0f;
        updateDisplayedXMulti();
    }

    public void addPoints(float points) {
        pointsValue += points;
        pointsPulseTime = 0f; // pulse only the points side

        updateDisplayedPoints();
    }

    public void addMulti(float multi) {
        multiValue += multi;
        multiPulseTime = 0f; // pulse only the multi side

        updateDisplayedMulti();
    }

    public void addStreak(float xMulti) {
        streakValue += xMulti;
        streakPulseTime = 0f;

        updateDisplayedXMulti();
    }

    private void updateDisplayedPoints() {
        Assets assetManager = Assets.I();
        long tempScore = (long) pointsValue;

        for(int i = pointDigitalNumbers.length -1; i >= 0; i--) {
            pointDigitalNumbers[i] = new TextureRegion(assetManager.getDigitalNumber(tempScore % 10));
            pointNumberShadows[i] = new TextureRegion(assetManager.getDigitalNumberShadow(tempScore % 10));
            tempScore /= 10;
        }
    }

    private void updateDisplayedMulti() {
        Assets assetManager = Assets.I();
        long tempScore = (long) multiValue;

        for(int i = multiDigitalNumbers.length -1; i >= 0; i--) {
            multiDigitalNumbers[i] = new TextureRegion(assetManager.getDigitalNumber(tempScore % 10));
            multiNumberShadows[i] = new TextureRegion(assetManager.getDigitalNumberShadow(tempScore % 10));
            tempScore /= 10;
        }
    }

    private void updateDisplayedXMulti() {
        Assets assetManager = Assets.I();
        long tempScore = (long) streakValue;

        for(int i = streakDigitalNumbers.length -1; i >= 0; i--) {
            streakDigitalNumbers[i] = new TextureRegion(assetManager.getDigitalNumber(tempScore % 10));
            streakNumberShadows[i] = new TextureRegion(assetManager.getDigitalNumberShadow(tempScore % 10));
            tempScore /= 10;
        }
    }

    public float getPoints() {
        return pointsValue;
    }

    public float getMulti() {
        return multiValue;
    }

    public float getXMulti() {
        return streakValue;
    }

    public boolean isEmpty() {
        return pointsValue == 0f && multiValue == 0f && streakValue == 0f;
    }

}
