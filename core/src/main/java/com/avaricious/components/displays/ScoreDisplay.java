package com.avaricious.components.displays;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScoreDisplay {

    private final Texture scoreDisplayTexture;
    private final Texture[] digitalNumbers = new Texture[7];

    private long score;
    private long displayedScore;

    private float hoverTime = 0f;

    public ScoreDisplay() {
        scoreDisplayTexture = Assets.I().getScoreBorder();
        for (int i = 0; i < digitalNumbers.length; i++) {
            digitalNumbers[i] = Assets.I().unlitNumber();
        }
    }

    public void draw(SpriteBatch batch, float delta) {
        if(displayedScore < score) {
            long diff = score - displayedScore;
            displayedScore += (long) Math.ceil(diff * 0.025);
            updateDigitalNumbers(displayedScore);
        }

        hoverTime += delta;
        float hoverOffset = (float) Math.sin(hoverTime * 1.5f/*hoverTime*/) * 0.03f/*hoverStrength*/;

        float baseY = 6.775f;
        float numberBaseY = 7.5f + hoverOffset;

//        batch.draw(scoreDisplayTexture, 0.75f, baseY, 3.84f, 1.32f);
        batch.setColor(Assets.I().lightColor());
        for (int i = 0; i < digitalNumbers.length; i++) {
            batch.draw(digitalNumbers[i], 8.175f + (i * 0.6f), numberBaseY, 0.32f * 1.5f, 0.56f * 1.5f);
        }
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void addToScore(long score) {
        this.score += score;
    }

    public void resetScore() {
        score = 0;
        updateDigitalNumbers(0);
    }

    private void updateDigitalNumbers(long score) {
        displayedScore = score;
        Assets assetManager = Assets.I();
        digitalNumbers[6] = assetManager.getDigitalNumber(score % 10);
        digitalNumbers[5] = assetManager.getDigitalNumber((score / 10) % 10);
        digitalNumbers[4] = assetManager.getDigitalNumber((score / 100) % 10);
        digitalNumbers[3] = assetManager.getDigitalNumber((score / 1000) % 10);
        digitalNumbers[2] = assetManager.getDigitalNumber((score / 10000) % 10);
        digitalNumbers[1] = assetManager.getDigitalNumber((score / 100000) % 10);
        digitalNumbers[0] = assetManager.getDigitalNumber((score / 1000000) % 10);
    }

}
