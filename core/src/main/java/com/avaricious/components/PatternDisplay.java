package com.avaricious.components;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PatternDisplay {

    private final Texture patternDisplayTexture;

    private final Texture[] pointDigitalNumbers = new Texture[3];
    private final Texture[] multiDigitalNumbers = new Texture[3];

    private long pointsValue = 0L;
    private long multiValue = 0L;

    private long displayedPoints = 0L;
    private long displayedMulti = 0L;

    public PatternDisplay() {
        patternDisplayTexture = Assets.I().getPatternDisplay();
        for(int i = 0; i < 3; i++) {
            pointDigitalNumbers[i] = Assets.I().unlitNumber();
            multiDigitalNumbers[i] = Assets.I().unlitNumber();
        }
    }


    public void draw(SpriteBatch batch) {
        if(displayedPoints != pointsValue) {
            long diff = Math.abs(pointsValue - displayedPoints);
            long change = (long) Math.ceil(diff * 0.025);
            displayedPoints = displayedPoints < pointsValue ? displayedPoints + change : displayedPoints - change;
            updateDisplayedPoints();
        }
        if(displayedMulti != multiValue) {
            long diff = Math.abs(multiValue - displayedMulti);
            long change = (long) Math.ceil(diff * 0.025);
            displayedMulti = displayedMulti < multiValue ? displayedMulti + change : displayedMulti - change;
            updateDisplayedMulti();
        }

        batch.draw(patternDisplayTexture, 0.75f, 4.75f, 3.84f, 1.6f);
        for(int i = 0; i < 3; i++) {
            batch.setColor(Assets.I().colorBlue());
            batch.draw(pointDigitalNumbers[i],1.21f + (i * 0.35f), 5.4f, 8 / 30f, 14 / 30f);
        }
        for(int i = 0; i < 3; i++) {
            batch.setColor(Assets.I().colorRed());
            batch.draw(multiDigitalNumbers[i],3.17f + (i * 0.35f), 5.4f, 8 / 30f, 14 / 30f);
        }
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void setPattern(String pattern) {
        if(pattern.isEmpty()) {
            pointsValue = 0L;
            multiValue = 0L;
            return;
        }
        String[] parts = pattern.split(" x ");

        pointsValue = Long.parseLong(parts[0]);
        multiValue = Long.parseLong(parts[1]);
    }

    private void updateDisplayedPoints() {
        Assets assetManager = Assets.I();
        if(displayedPoints == 0L) {
            for(int i = 0; i < 3; i++) {
                pointDigitalNumbers[i] = assetManager.unlitNumber();
            }
        } else {
            this.pointDigitalNumbers[2] = assetManager.getDigitalNumber(displayedPoints % 10);
            this.pointDigitalNumbers[1] = assetManager.getDigitalNumber((displayedPoints / 10) % 10);
            this.pointDigitalNumbers[0] = assetManager.getDigitalNumber((displayedPoints / 100) % 10);
        }
    }

    private void updateDisplayedMulti() {
        Assets assetManager = Assets.I();
        if(displayedMulti == 0L) {
            for(int i = 0; i < 3; i++) {
                multiDigitalNumbers[i] = Assets.I().unlitNumber();
            }
        } else {
            this.multiDigitalNumbers[2] = assetManager.getDigitalNumber(displayedMulti % 10);
            this.multiDigitalNumbers[1] = assetManager.getDigitalNumber((displayedMulti / 10) % 10);
            this.multiDigitalNumbers[0] = assetManager.getDigitalNumber((displayedMulti / 100) % 10);
        }
    }
}
