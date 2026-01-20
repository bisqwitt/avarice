package com.avaricious.components.progressbar;

import com.avaricious.Assets;
import com.avaricious.DevTools;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class HealthBar extends ProgressBar {

    private final Texture heart = Assets.I().getHeart();
    private final Texture box = Assets.I().getJokerCardShadow();
    private float currentValue;

    public HealthBar(float maxHealth) {
        super(100, Assets.I().getHealthRedPixel());
        setMaxValue(maxHealth);
    }

    public void setCurrentHealth(float currentHealth) {
        currentValue = MathUtils.clamp(currentHealth, 0f, getMaxValue());
    }

    @Override
    public void draw(SpriteBatch batch) {
        float diff = Math.abs(currentValue - getDisplayedValue());
        float nextStep = diff / 30 < 5 ? 5 : diff / 30;
        setDisplayedValue(diff < 1 ? currentValue : currentValue > getDisplayedValue()
            ? getDisplayedValue() + diff / 30 : getDisplayedValue() - diff / 30);

        batch.setColor(1f, 1f, 1f, 0.25f);
        batch.draw(box, 12.95f, 1.2f, 1.5f, 5.4f);
        batch.setColor(1f, 1f, 1f, 1f);

        for(int i = 0; i < progress.length; i++) {
            batch.draw(progress[i], 13.325f, 2f + (i * 0.04325f), 3 / 22f, 2 / 22f);
        }
//        batch.draw(border, 14.8f, 3.7f, 14 / 70f, 310 / 70f);
        batch.draw(heart, 13.15f, 1.4f, 12 / 25f, 11 / 25f);
    }

    public float getMaxHealth() {
        return getMaxValue();
    }

    public float getCurrentHealth() {
        return currentValue;
    }

    public void damage(float amount) {
        if(!DevTools.noDamage) setCurrentHealth(currentValue - amount);
    }

    public void heal(float amount) {
        setCurrentHealth(currentValue + amount);
    }
}
