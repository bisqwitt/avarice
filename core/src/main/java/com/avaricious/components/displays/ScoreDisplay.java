package com.avaricious.components.displays;

import com.avaricious.Assets;
import com.avaricious.RoundsManager;
import com.avaricious.components.DigitalNumber;
import com.avaricious.components.progressbar.ProgressBar;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ScoreDisplay {
    private final DigitalNumber digitalNumber;
    private final ProgressBar progressBar;
    private final Texture backgroundBox = Assets.I().getScoreDisplayBackground();
    private final Texture whiteTexture = Assets.I().getWhiteTexture();

    public ScoreDisplay() {
        progressBar = new ProgressBar();
        progressBar.setMaxValue(RoundsManager.I().getCurrentTargetScore());
        digitalNumber = new DigitalNumber(0, Assets.I().lightColor(), 7,
            new Rectangle(5.1f, 7.15f, 0.32f * 2f, 0.56f * 2f), 0.8f);
    }

    public void draw(SpriteBatch batch, float delta) {
//        progressBar.draw(batch);
//        batch.draw(backgroundBox, 4.5f, 6.3f, 273 / 40f, 88 / 40f);

        batch.draw(whiteTexture, 2.65f, 6.65f, 10.25f, 0.1f);
        digitalNumber.draw(batch, delta);
    }

    public void addToScore(int amount) {
        digitalNumber.setScore(digitalNumber.getScore() + amount);
        progressBar.setDisplayedValue(digitalNumber.getScore());
    }

    public void resetScore() {
        digitalNumber.setScore(0);
    }

    public boolean targetScoreReached() {
        return digitalNumber.getScore() >= RoundsManager.I().getCurrentTargetScore();
    }

    public void nextRound() {
        resetScore();
        progressBar.setMaxValue(RoundsManager.I().getCurrentTargetScore());
    }

}
