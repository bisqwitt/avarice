package com.avaricious.components.progressbar;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TimedProgressBar extends ProgressBar {

    private float elapsed = 0f;

    public TimedProgressBar(float reachInSeconds) {
        setMaxValue(reachInSeconds);
        setDisplayedValue(0f);
    }

    public void render(SpriteBatch batch, float delta) {
        // progress time
        elapsed += delta;

        // cap it at maxValue (reachInSeconds)
        if (elapsed > getMaxValue()) {
            elapsed = getMaxValue();
        }

        // set current value
        setDisplayedValue(elapsed);

        // draw the bar
        super.draw(batch);
    }

    public void reset() {
        elapsed = 0f;
        setDisplayedValue(0f);
    }

    public void restart(float reachInSeconds) {
        setMaxValue(reachInSeconds);
        reset();
    }
}
