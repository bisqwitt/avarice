package com.avaricious.components.displays;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PatternDisplay {

    private final Texture patternDisplayTexture;

    private final Texture[] pointDigitalNumbers = new Texture[3];
    private final Texture[] multiDigitalNumbers = new Texture[3];

    private float pointsValue = 0L;
    private float multiValue = 0L;

    private float displayedPoints = 0L;
    private float displayedMulti = 0L;

    private float startPoints;
    private float startMulti;

    private float animTime = 0f;
    private float animDuration = 0.5f; // 0.5 seconds, adjust as you like

    private float hoverTime = 0f;

    public PatternDisplay() {
        patternDisplayTexture = Assets.I().getPatternDisplay();
        for(int i = 0; i < 3; i++) {
            pointDigitalNumbers[i] = Assets.I().getDigitalNumber(0);
            multiDigitalNumbers[i] = Assets.I().getDigitalNumber(0);
        }
    }


    public void draw(SpriteBatch batch, float delta) {
        updateDisplayedNumbers(delta);

        hoverTime += delta;
        float hoverOffset = (float) Math.sin(hoverTime * 1.5f/*hoverTime*/) * 0.03f/*hoverStrength*/;
        float baseY = 5.025f;
        float numberBaseY = 5.675f + hoverOffset;

//        batch.draw(patternDisplayTexture, 0.75f, baseY, 3.84f, 1.6f);
        for(int i = 0; i < 3; i++) {
            batch.setColor(Assets.I().colorBlue());
            batch.draw(pointDigitalNumbers[i],1.21f + (i * 0.35f), numberBaseY, 8 / 30f, 14 / 30f);
        }
        for(int i = 0; i < 3; i++) {
            batch.setColor(Assets.I().colorRed());
            batch.draw(multiDigitalNumbers[i],3.17f + (i * 0.35f), numberBaseY, 8 / 30f, 14 / 30f);
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
    }

    public void addPoints(float points) {
        pointsValue += points;
        startPoints = displayedPoints;
    }

    public void addMulti(float multi) {
        multiValue += multi;
        startMulti = displayedMulti;
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
                pointDigitalNumbers[i] = assetManager.unlitNumber();
            }
        } else {
            long asLong = (long) displayedPoints;
            this.pointDigitalNumbers[2] = assetManager.getDigitalNumber(asLong % 10);
            this.pointDigitalNumbers[1] = assetManager.getDigitalNumber((asLong / 10) % 10);
            this.pointDigitalNumbers[0] = assetManager.getDigitalNumber((asLong / 100) % 10);
        }
    }

    private void updateDisplayedMulti() {
        Assets assetManager = Assets.I();
        if(displayedMulti == 0L) {
            for(int i = 0; i < 3; i++) {
                multiDigitalNumbers[i] = Assets.I().unlitNumber();
            }
        } else {
            long asLong = (long) displayedMulti;
            this.multiDigitalNumbers[2] = assetManager.getDigitalNumber(asLong % 10);
            this.multiDigitalNumbers[1] = assetManager.getDigitalNumber((asLong / 10) % 10);
            this.multiDigitalNumbers[0] = assetManager.getDigitalNumber((asLong / 100) % 10);
        }
    }
}
