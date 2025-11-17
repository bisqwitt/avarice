package com.avaricious;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoreDisplay {

    private final Texture scoreDisplayTexture;
    private final Texture[] digitalNumbers = new Texture[7];

    public ScoreDisplay() {
        scoreDisplayTexture = Assets.I().getScoreBorder();
        for (int i = 0; i < digitalNumbers.length; i++) {
            digitalNumbers[i] = Assets.I().unlitNumber();
        }
    }

    public void setScore(int score) {
        Assets assetManager = Assets.I();

        digitalNumbers[6] = assetManager.getDigitalNumber(score % 10);
        digitalNumbers[5] = assetManager.getDigitalNumber((score / 10) % 10);
        digitalNumbers[4] = assetManager.getDigitalNumber((score / 100) % 10);
        digitalNumbers[3] = assetManager.getDigitalNumber((score / 1000) % 10);
        digitalNumbers[2] = assetManager.getDigitalNumber((score / 10000) % 10);
        digitalNumbers[1] = assetManager.getDigitalNumber((score / 100000) % 10);
        digitalNumbers[0] = assetManager.getDigitalNumber((score / 1000000) % 10);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(scoreDisplayTexture, 0.75f, 6.5f, 3.84f, 1.32f);
        for(int i = 0; i < digitalNumbers.length; i++) {
            batch.draw(digitalNumbers[i], 1.325f + (i * 0.4f), 6.935f, 0.32f, 0.56f);
        }
    }

}
