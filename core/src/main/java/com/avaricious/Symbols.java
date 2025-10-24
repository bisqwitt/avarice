package com.avaricious;

public enum Symbols {

    LEMON("lemon.png", 2),
    CHERRY("cherry.png", 2),
    CLOVER("clover.png", 3),
    BELL("bell.png", 3),
    IRON("iron.png", 5),
    DIAMOND("diamond.png", 5),
    SEVEN("seven.png", 7);

    private final String path;
    private final int baseValue;

    Symbols(String path, int value) {
        this.path = path;
        this.baseValue = value;
    }

    public String path() {
        return path;
    }

    public int baseValue() {
        return baseValue;
    }
}
