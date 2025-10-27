package com.avaricious.upgrades.symbol;

import com.avaricious.symbol.Symbol;
import com.avaricious.slot.SymbolManager;
import com.avaricious.upgrades.Upgrade;

public class SymbolValueUpgrade implements Upgrade {

    private final Symbol symbol;
    private final Integer amount;

    public SymbolValueUpgrade(Symbol symbol, Integer amount) {
        this.symbol = symbol;
        this.amount = amount;
    }

    @Override
    public void apply() {
        SymbolManager.I().changeValue(symbol, amount);
    }

    @Override
    public String description() {
        return "Increase " + symbol.toString() + "'s base value by " + amount;
    }
}
