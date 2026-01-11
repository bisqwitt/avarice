package com.avaricious.components.slot;

import com.avaricious.stats.statupgrades.StatUpgrade;

public class SymbolInstance {

    private final Symbol symbol;
    private final StatUpgrade statUpgrade;

    public SymbolInstance(Symbol symbol, StatUpgrade statUpgrade) {
        this.symbol = symbol;
        this.statUpgrade = statUpgrade;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public StatUpgrade getStatUpgrade() {
        return statUpgrade;
    }

}
