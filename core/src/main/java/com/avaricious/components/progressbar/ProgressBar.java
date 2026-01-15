package com.avaricious.components.progressbar;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ProgressBar {

    protected final TextureRegion border;
    protected final TextureRegion lit;
    private final TextureRegion unlit;

    protected TextureRegion[] progress;

    private int steps;
    private float maxValue = 0f;
    private float displayedValue = 0f;

    // Existing layout values (you likely want to revisit spacing; see note below)
    private final float startX = 1f;
    private final float stepX = 0.04325f;
    private final float y = 7.2f;

    public ProgressBar() {
        this(100);
    }

    public ProgressBar(int steps) {
        border = new TextureRegion(Assets.I().getProgressBarBorder());
        lit = new TextureRegion(Assets.I().getProgressLit());
        unlit = new TextureRegion(Assets.I().getProgressUnlit());

        setSteps(steps);
    }

    protected ProgressBar(int steps, Texture litTexture) {
        border = new TextureRegion(Assets.I().getProgressBarBorder());
        lit = new TextureRegion(litTexture);
        unlit = new TextureRegion(Assets.I().getProgressUnlit());

        setSteps(steps);
    }

    public void setSteps(int steps) {
        if (steps <= 0) throw new IllegalArgumentException("steps must be > 0");
        this.steps = steps;

        progress = new TextureRegion[steps];
        for (int i = 0; i < steps; i++) {
            progress[i] = unlit;
        }
        updateProgressTextures();
    }

    public int getSteps() {
        return steps;
    }

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < progress.length; i++) {
            batch.draw(
                progress[i],
                startX + (i * stepX), y,
                (3/22f) / 2f, (2/22f) / 2f,
                3 / 22f, 2 / 22f,
                1f, 1f,
                90f
            );
        }

        batch.draw(
            border,
            3.1f, 5f,
            (14/70f) / 2f,
            (310/70f) / 2f,
            14 / 70f,
            310 / 70f,
            1f, 1f,
            90f
        );
    }

    protected void updateProgressTextures() {
        if (steps <= 0) return;

        float max = Math.max(0f, maxValue);
        float shown = displayedValue;

        // Clamp displayedValue into [0, max] when max > 0, otherwise treat as 0%
        float percent;
        if (max <= 0f) {
            percent = 0f;
        } else {
            shown = Math.max(0f, Math.min(shown, max));
            percent = shown / max; // in [0,1]
        }

        // Use ceil if you want "small progress shows at least 1 segment".
        // Use floor if you want "must fill whole segment to light it".
        int litCount = Math.round(percent * steps);

        // Clamp litCount into [0, steps]
        litCount = Math.max(0, Math.min(litCount, steps));

        for (int i = 0; i < steps; i++) {
            progress[i] = (i < litCount) ? lit : unlit;
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
