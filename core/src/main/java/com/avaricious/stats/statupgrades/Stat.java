package com.avaricious.stats.statupgrades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class Stat {

    private BigDecimal percentageChance = new BigDecimal("0.05");

    public abstract Texture getTexture();

    public abstract Texture getShadowTexture();

    public boolean rollChance() {
        float rng = MathUtils.random();
        return rng < percentageChance.floatValue();
    }

    public BigDecimal getPercentageChance() {
        return percentageChance;
    }

    public int getPercentageAsNumber() {
        return (int) (percentageChance.floatValue() * 100);
    }

    public void setPercentageChance(BigDecimal percentageChance) {
        this.percentageChance = percentageChance;
    }
}
