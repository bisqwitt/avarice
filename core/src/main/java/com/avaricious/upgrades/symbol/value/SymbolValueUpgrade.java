package com.avaricious.upgrades.symbol.value;

import com.avaricious.slot.Symbol;
import com.avaricious.slot.SymbolManager;
import com.avaricious.upgrades.Upgrade;

public abstract class SymbolValueUpgrade implements Upgrade {

    private final Symbol symbol;
    private final Integer amount;

    protected SymbolValueUpgrade(Symbol symbol) {
        this.symbol = symbol;
        this.amount = 10;
    }

    @Override
    public void apply() {
        SymbolManager.I().changeValue(symbol, amount);
    }

    @Override
    public String description() {
        return "Increase " + symbol.toString() + "'s value by " + amount;
    }

}
