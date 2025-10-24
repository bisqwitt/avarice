package com.avaricious;

public class PatternHit {

    private final Symbols symbol;
    private final PatternType type;

    PatternHit(Symbols symbol, PatternType type) {
        this.symbol = symbol;
        this.type = type;
    }

    PatternHit(Symbols symbol, int length) {
        this(symbol, length == 3 ? PatternType.HOR
            : length == 4 ? PatternType.HOR_L
            : PatternType.HOR_XL);
    }

    public Symbols symbol() {
        return symbol;
    }

    public PatternType type() {
        return type;
    }
}
