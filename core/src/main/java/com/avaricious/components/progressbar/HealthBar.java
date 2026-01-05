package com.avaricious.components.progressbar;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class HealthBar extends ProgressBar {

    private float currentValue;

    public HealthBar(float maxHealth) {
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

        for(int i = 0; i < progress.length; i++) {
            batch.draw(progress[i], 14.85f, 3.76f + (i * 0.04325f), 3 / 22f, 2 / 22f);
        }
        batch.draw(border, 14.8f, 3.7f, 14 / 70f, 310 / 70f);
    }

    public void fullHeal() {
        setCurrentHealth(getMaxValue());
    }

    public float getMaxHealth() {
        return getMaxValue();
    }

    public float getCurrentHealth() {
        return currentValue;
    }

    public void damage(float amount) {
        setCurrentHealth(currentValue - amount);
    }

    public void heal(float amount) {
        setCurrentHealth(currentValue + amount);
    }
}
