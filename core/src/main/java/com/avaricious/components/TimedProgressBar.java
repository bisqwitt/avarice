package com.avaricious.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TimedProgressBar extends ProgressBar {

    private float elapsed = 0f;

    public TimedProgressBar(float reachInSeconds) {
        setMaxValue(reachInSeconds);
        setCurrentValue(0f);
    }

    public void render(SpriteBatch batch, float delta) {
        // progress time
        elapsed += delta;

        // cap it at maxValue (reachInSeconds)
        if (elapsed > getMaxValue()) {
            elapsed = getMaxValue();
        }

        // set current value
        setCurrentValue(elapsed);

        // draw the bar
        super.render(batch);
    }

    public void reset() {
        elapsed = 0f;
        setCurrentValue(0f);
    }
}
