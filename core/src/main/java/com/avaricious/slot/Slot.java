package com.avaricious.slot;

import com.avaricious.symbol.Symbol;

public class Slot {

    private final float posX;
    private final float posY;

    private Symbol type;

    public Slot(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;

        spin();
    }

    public void spin() {
        type = SymbolManager.I().randomSymbol();
    }

    public Symbol type() {
        return type;
    }
    public void setType(Symbol type) {}

    public float posX() {
        return posX;
    }

    public float posY() {
        return posY;
    }

}
