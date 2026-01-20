package com.avaricious.stats.statupgrades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import java.math.BigDecimal;

public abstract class Stat {

    private BigDecimal percentageChance = new BigDecimal("0.01");

    public abstract Texture getTexture();

    public abstract Texture getShadowTexture();

    public boolean rollChance() {
        float rng = MathUtils.random();
        return rng < percentageChance.floatValue();
    }

    public BigDecimal getPercentage() {
        return percentageChance;
    }

    public void setPercentage(BigDecimal percentageChance) {
        this.percentageChance = percentageChance;
    }
}
