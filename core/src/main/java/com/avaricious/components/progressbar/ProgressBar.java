package com.avaricious.components.progressbar;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ProgressBar {

    protected final TextureRegion border;
    private final TextureRegion lit;
    private final TextureRegion unlit;
    protected TextureRegion[] progress = new TextureRegion[100];

    private float maxValue = 0f;
    private float displayedValue = 0f;

    public ProgressBar() {
        border = new TextureRegion(Assets.I().getProgressBarBorder());
        lit = new TextureRegion(Assets.I().getProgressLit());
        unlit = new TextureRegion(Assets.I().getProgressUnlit());

        for (int i = 0; i < 100; i++) {
            progress[i] = unlit;
        }
    }

    public void draw(SpriteBatch batch) {
        for(int i = 0; i < progress.length; i++) {
            batch.draw(
                progress[i],
                1f + (i * 0.04325f), 7.2f,
                (3/22f) / 2f, (2/22f) / 2f,
                3 / 22f, 2 / 22f,
                1f, 1f,
                90f);
        }
        batch.draw(
            border,
            3.1f, 5f,         // position
            (14/70f) / 2f,       // originX (half width)
            (310/70f) / 2f,      // originY (half height)
            14 / 70f,            // width
            310 / 70f,           // height
            1f, 1f,              // scaleX, scaleY
            90f                  // rotation in degrees
        );
    }

    protected void updateProgressTextures() {
        float percent = (maxValue == 0) ? 0 : displayedValue / maxValue;
        int litCount = Math.round(percent * 100);

        for (int i = 0; i < 100; i++) {
            if (i < litCount) {
                progress[i] = lit;
            } else {
                progress[i] = unlit;
            }
        }
    }


    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        updateProgressTextures();
    }

    public void setDisplayedValue(float displayedValue) {
        this.displayedValue = displayedValue;
        updateProgressTextures();
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getDisplayedValue() {
        return displayedValue;
    }
}
