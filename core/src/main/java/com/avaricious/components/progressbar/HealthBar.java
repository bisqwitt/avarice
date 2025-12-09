package com.avaricious.components.progressbar;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class HealthBar extends ProgressBar {

    private float maxHealth;
    private float currentHealth;

    public HealthBar(float maxHealth) {
        super();
        setMaxHealth(maxHealth);
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
        // delegate to ProgressBar logic
        setMaxValue(maxHealth);
    }

    public void setCurrentHealth(float currentHealth) {
        // clamp between 0 and maxHealth for safety
        this.currentHealth = MathUtils.clamp(currentHealth, 0f, maxHealth);
        // delegate to ProgressBar logic
        setCurrentValue(this.currentHealth);
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public void damage(float amount) {
        setCurrentHealth(currentHealth - amount);
    }

    public void heal(float amount) {
        setCurrentHealth(currentHealth + amount);
    }

    // Option A: just expose render as public, using the ProgressBar's fixed position
    @Override
    public void draw(SpriteBatch batch) {
        for(int i = 0; i < progress.length; i++) {
            batch.draw(progress[i], 14.85f, 3.76f + (i * 0.04325f), 3 / 22f, 2 / 22f);
        }
        batch.draw(border, 14.8f, 3.7f, 14 / 70f, 310 / 70f);
    }
}
