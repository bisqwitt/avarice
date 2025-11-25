package com.avaricious.components.displays;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PatternDisplay {

    private final Texture multTexture;

    private final TextureRegion[] pointDigitalNumbers = new TextureRegion[3];
    private final TextureRegion[] multiDigitalNumbers = new TextureRegion[3];
    private final TextureRegion[] xMultiDigitalNumbers = new TextureRegion[2];

    private float pointsValue = 0L;
    private float multiValue = 0L;
    private float xMultiValue = 0L;

    private float pointsPulseTime = 0f;
    private float multiPulseTime  = 0f;
    private float xMultiPulseTime = 0f;

    private float hoverTime = 0f;

    public PatternDisplay() {
        multTexture = Assets.I().mult();
        for(int i = 0; i < 3; i++) {
            pointDigitalNumbers[i] = new TextureRegion(Assets.I().getDigitalNumber(0));
            multiDigitalNumbers[i] = new TextureRegion(Assets.I().getDigitalNumber(0));
        }
        xMultiDigitalNumbers[0] = new TextureRegion(Assets.I().getDigitalNumber(0));
        xMultiDigitalNumbers[1] = new TextureRegion(Assets.I().getDigitalNumber(0));
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
        float xMultiScale = 1f;
        float xMultiRotation = 0f;
        if (xMultiPulseTime < pulseDuration) {
            xMultiPulseTime += delta;
            float t = xMultiPulseTime / pulseDuration;
            if (t > 1f) t = 1f;

            float pulseCurve = 1f - 4f * (t - 0.5f) * (t - 0.5f);
            if (pulseCurve < 0f) pulseCurve = 0f;

            float baseScale = 1.0f;
            float pulseScale = 0.35f;
            xMultiScale = baseScale + pulseCurve * pulseScale;

            float wobbleAngle = 8f;
            xMultiRotation = pulseCurve * wobbleAngle;
        }

        // Common digit size/origin
        float digitWidth  = 8 / 25f;
        float digitHeight = 14 / 25f;
        float originX = digitWidth / 2f;
        float originY = digitHeight / 2f;

        // Draw POINTS (blue)
        batch.setColor(Assets.I().colorBlue());
        for (int i = 0; i < 3; i++) {
            float x = 1.57f + (i * 0.35f);

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
        batch.setColor(Assets.I().colorRed());
        for (int i = 0; i < 3; i++) {
            float x = 3.17f + (i * 0.35f);

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
        batch.setColor(Assets.I().colorRed());
        for (int i = 0; i < 2; i++) {
            float x = 4.77f + (i * 0.35f);

            batch.draw(
                xMultiDigitalNumbers[i],
                x - originX,
                numberBaseY - originY,
                originX,
                originY,
                digitWidth,
                digitHeight,
                xMultiScale,
                xMultiScale,
                xMultiRotation
            );
        }

        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(multTexture, 2.56f, numberBaseY - originY, 0.35f, 0.35f);
        batch.draw(multTexture, 4.16f, numberBaseY - originY, 0.35f, 0.35f);
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

        xMultiPulseTime = 0f;
        xMultiValue = 0f;
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

    public void addXMulti(float xMulti) {
        xMultiValue += xMulti;
        xMultiPulseTime = 0f;

        updateDisplayedXMulti();
    }

    private void updateDisplayedPoints() {
        Assets assetManager = Assets.I();
        long asLong = (long) pointsValue;
        this.pointDigitalNumbers[2] = new TextureRegion(assetManager.getDigitalNumber(asLong % 10));
        this.pointDigitalNumbers[1] = new TextureRegion(assetManager.getDigitalNumber((asLong / 10) % 10));
        this.pointDigitalNumbers[0] = new TextureRegion(assetManager.getDigitalNumber((asLong / 100) % 10));
    }

    private void updateDisplayedMulti() {
        Assets assetManager = Assets.I();
        long asLong = (long) multiValue;
        this.multiDigitalNumbers[2] = new TextureRegion(assetManager.getDigitalNumber(asLong % 10));
        this.multiDigitalNumbers[1] = new TextureRegion(assetManager.getDigitalNumber((asLong / 10) % 10));
        this.multiDigitalNumbers[0] = new TextureRegion(assetManager.getDigitalNumber((asLong / 100) % 10));
    }

    private void updateDisplayedXMulti() {
        Assets assetManager = Assets.I();
        long asLong = (long) xMultiValue;
        this.xMultiDigitalNumbers[1] = new TextureRegion(assetManager.getDigitalNumber(asLong % 10));
        this.xMultiDigitalNumbers[0] = new TextureRegion(assetManager.getDigitalNumber((asLong / 10) % 10));
    }

    public float getPoints() {
        return pointsValue;
    }

    public float getMulti() {
        return multiValue;
    }

    public float getXMulti() {
        return xMultiValue;
    }

    public void triggerXMultAnimation() {
        xMultiPulseTime = 0f;
    }
}
