package com.avaricious.symbol;

public enum Symbol {

    LEMON("lemon.png", 2, 19.4f),
    CHERRY("cherry.png", 2, 19.4f),
    CLOVER("clover.png", 3, 14.9f),
    BELL("bell.png", 3, 14.9f),
    IRON("iron.png", 5, 11.9f),
    DIAMOND("diamond.png", 5, 11.9f),
    SEVEN("seven.png", 7, 7.5f);

    private final String path;
    private final Integer baseValue;
    private final Float baseSpawnChance;

    Symbol(String path, Integer baseValue, Float baseSpawnChance) {
        this.path = path;
        this.baseValue = baseValue;
        this.baseSpawnChance = baseSpawnChance;
    }

    public String path() {
        return path;
    }

    public Integer baseValue() {
        return baseValue;
    }
    public Float baseSpawnChance() {
        return baseSpawnChance;
    }

    public String toString() {
        return this.name().replace(".png", "");
    }
}
