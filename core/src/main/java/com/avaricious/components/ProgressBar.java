package com.avaricious.components;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ProgressBar {

    private final Texture border;
    private final Texture lit;
    private final Texture unlit;
    private Texture[] progress = new Texture[100];


    private float maxValue = 0f;
    private float currentValue = 0f;

    public ProgressBar() {
        border = Assets.I().getProgressBarBorder();
        lit = Assets.I().getProgressLit();
        unlit = Assets.I().getProgressUnlit();

        for (int i = 0; i < 100; i++) {
            progress[i] = unlit;
        }
    }


    protected void render(SpriteBatch batch) {
        for(int i = 0; i < progress.length; i++) {
            batch.draw(progress[i], 14.05f, 2.56f + (i * 0.0505f), 3 / 22f, 2 / 22f);
        }
        batch.draw(border, 14f, 2.5f, 14 / 60f, 310 / 60f);
    }

    private void updateProgressTextures() {
        float percent = (maxValue == 0) ? 0 : currentValue / maxValue;
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

    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;
        updateProgressTextures();
    }

    protected float getMaxValue() {
        return maxValue;
    }

}
