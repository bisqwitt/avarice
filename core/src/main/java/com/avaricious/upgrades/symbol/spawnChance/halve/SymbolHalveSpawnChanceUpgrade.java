package com.avaricious.upgrades.symbol.spawnChance.halve;

import com.avaricious.slot.Symbol;
import com.avaricious.upgrades.symbol.spawnChance.SymbolSpawnChanceUpgrade;

public abstract class SymbolHalveSpawnChanceUpgrade extends SymbolSpawnChanceUpgrade {
    protected SymbolHalveSpawnChanceUpgrade(Symbol symbol) {
        super(symbol, false);
    }
}
