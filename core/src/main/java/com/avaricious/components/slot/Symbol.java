package com.avaricious.components.slot;

public enum Symbol {

    LEMON(2, 19.4f),
    CHERRY(2, 19.4f),
    CLOVER(3, 14.9f),
    BELL(3, 14.9f),
    IRON(5, 11.9f),
    DIAMOND(5, 11.9f),
    SEVEN(7, 7.5f);

    private Integer baseValue;
    private final Float baseSpawnChance;

    Symbol(Integer baseValue, Float baseSpawnChance) {
        this.baseValue = baseValue;
        this.baseSpawnChance = baseSpawnChance;
    }

    public Integer baseValue() {
        return baseValue;
    }

    public void setBaseValue(int value) {
        baseValue = value;
    }
    public Float baseSpawnChance() {
        return baseSpawnChance;
    }

    public String toString() {
        return this.name().replace(".png", "");
    }
}
