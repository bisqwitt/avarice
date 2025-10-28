package com.avaricious.upgrades.symbol.spawnChance.increase;

import com.avaricious.slot.Symbol;
import com.avaricious.upgrades.symbol.spawnChance.SymbolSpawnChanceUpgrade;

public abstract class SymbolIncSpawnChanceUpgrade extends SymbolSpawnChanceUpgrade {
    protected SymbolIncSpawnChanceUpgrade(Symbol symbol) {
        super(symbol, true);
    }
}
