package com.avaricious.components.displays;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PatternDisplay {

    private final Texture patternDisplayTexture;

    private final TextureRegion[] pointDigitalNumbers = new TextureRegion[3];
    private final TextureRegion[] multiDigitalNumbers = new TextureRegion[3];

    private float pointsValue = 0L;
    private float multiValue = 0L;

    private float displayedPoints = 0L;
    private float displayedMulti = 0L;

    private float startPoints;
    private float startMulti;

    private float animTime = 0f;
    private float animDuration = 0.5f; // 0.5 seconds, adjust as you like

    // Pulse animation for the numbers (Balatro-style bump)
    private float pointsPulseTime = 0f;
    private float multiPulseTime  = 0f;
    private final float pulseDuration = 0.2f; // seconds for the pulse


    private float hoverTime = 0f;

    public PatternDisplay() {
        patternDisplayTexture = Assets.I().getPatternDisplay();
        for(int i = 0; i < 3; i++) {
            pointDigitalNumbers[i] = new TextureRegion(Assets.I().getDigitalNumber(0));
            multiDigitalNumbers[i] = new TextureRegion(Assets.I().getDigitalNumber(0));
        }
    }


    public void draw(SpriteBatch batch, float delta) {
        updateDisplayedNumbers(delta);

        hoverTime += delta;
        float hoverOffset = (float) Math.sin(hoverTime * 1.5f) * 0.03f;
        float baseY = 5.025f;
        float numberBaseY = 5.675f + hoverOffset;

        // ----- Pulse + wobble for POINTS -----
        float pointsScale = 1f;
        float pointsRotation = 0f;
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

        // Common digit size/origin
        float digitWidth  = 8 / 25f;
        float digitHeight = 14 / 25f;
        float originX = digitWidth / 2f;
        float originY = digitHeight / 2f;

        // Draw POINTS (blue)
        batch.setColor(Assets.I().colorBlue());
        for (int i = 0; i < 3; i++) {
            float x = 1.21f + (i * 0.35f);

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

        batch.setColor(1f, 1f, 1f, 1f);
    }


    public void setPattern(String pattern) {
        if(pattern.isEmpty()) {
            pointsValue = 0L;
            multiValue = 0L;
        } else {
            String[] parts = pattern.split(" x ");
            pointsValue = Long.parseLong(parts[0]);
            multiValue = Long.parseLong(parts[1]);
        }

        startPoints = displayedPoints;
        startMulti = displayedMulti;

        animTime = 0f;

        // Trigger a pulse for both displays
        pointsPulseTime = 0f;
        multiPulseTime  = 0f;
    }

    public void addPoints(float points) {
        pointsValue += points;
        startPoints = displayedPoints;

        animTime = 0f;
        pointsPulseTime = 0f; // pulse only the points side
    }

    public void addMulti(float multi) {
        multiValue += multi;
        startMulti = displayedMulti;

        animTime = 0f;
        multiPulseTime = 0f; // pulse only the multi side
    }


    private void updateDisplayedNumbers(float delta) {
        if (animTime < animDuration) {
            animTime += delta;
            float t = animTime / animDuration;
            if (t > 1f) t = 1f;

            // Optional: ease instead of linear
            float eased = com.badlogic.gdx.math.Interpolation.sineOut.apply(t);

            displayedPoints = com.badlogic.gdx.math.MathUtils.lerp(startPoints,  pointsValue, eased);
            displayedMulti  = com.badlogic.gdx.math.MathUtils.lerp(startMulti,   multiValue,  eased);
        }

        if(displayedPoints != pointsValue) {
            float diff = Math.abs(pointsValue - displayedPoints);
            float change = (float) Math.ceil(diff * 0.025);
            displayedPoints = displayedPoints < pointsValue ? displayedPoints + change : displayedPoints - change;
            updateDisplayedPoints();
        }
        if(displayedMulti != multiValue) {
            float diff = Math.abs(multiValue - displayedMulti);
            float change = (float) Math.ceil(diff * 0.025);
            displayedMulti = displayedMulti < multiValue ? displayedMulti + change : displayedMulti - change;
            updateDisplayedMulti();
        }
    }

    private void updateDisplayedPoints() {
        Assets assetManager = Assets.I();
        if(displayedPoints == 0L) {
            for(int i = 0; i < 3; i++) {
                pointDigitalNumbers[i] = new TextureRegion(assetManager.unlitNumber());
            }
        } else {
            long asLong = (long) displayedPoints;
            this.pointDigitalNumbers[2] = new TextureRegion(assetManager.getDigitalNumber(asLong % 10));
            this.pointDigitalNumbers[1] = new TextureRegion(assetManager.getDigitalNumber((asLong / 10) % 10));
            this.pointDigitalNumbers[0] = new TextureRegion(assetManager.getDigitalNumber((asLong / 100) % 10));
        }
    }

    private void updateDisplayedMulti() {
        Assets assetManager = Assets.I();
        if(displayedMulti == 0L) {
            for(int i = 0; i < 3; i++) {
                multiDigitalNumbers[i] = new TextureRegion(Assets.I().unlitNumber());
            }
        } else {
            long asLong = (long) displayedMulti;
            this.multiDigitalNumbers[2] = new TextureRegion(assetManager.getDigitalNumber(asLong % 10));
            this.multiDigitalNumbers[1] = new TextureRegion(assetManager.getDigitalNumber((asLong / 10) % 10));
            this.multiDigitalNumbers[0] = new TextureRegion(assetManager.getDigitalNumber((asLong / 100) % 10));
        }
    }
}
