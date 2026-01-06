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

    public ScoreDisplay() {
        progressBar = new ProgressBar();
        progressBar.setMaxValue(RoundsManager.I().getCurrentTargetScore());
        digitalNumber = new DigitalNumber(0, Assets.I().lightColor(), 7,
            new Rectangle(1.175f, 7.5f, 0.32f * 1.5f, 0.56f * 1.5f), 0.6f);
    }

    public void draw(SpriteBatch batch, float delta) {
        progressBar.draw(batch);
        digitalNumber.draw(batch, delta);
    }

    public void addToScore(int amount) {
        digitalNumber.setScore(digitalNumber.getScore() + amount);
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
