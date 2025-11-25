package com.avaricious.components.slot;

import com.avaricious.upgrades.UpgradesManager;

import java.util.HashMap;
import java.util.Map;

public class SymbolManager {

    private static SymbolManager instance;
    public static SymbolManager I() {
        return instance ==  null ? instance = new SymbolManager() : instance;
    }

    private SymbolManager() {}

    private final Map<Symbol, Float> symbolSpawnChanceMap = new HashMap<Symbol, Float>() {{
        put(Symbol.LEMON, Symbol.LEMON.baseSpawnChance());
        put(Symbol.CHERRY, Symbol.CHERRY.baseSpawnChance());
        put(Symbol.CLOVER, Symbol.CLOVER.baseSpawnChance());
        put(Symbol.BELL, Symbol.BELL.baseSpawnChance());
        put(Symbol.IRON, Symbol.IRON.baseSpawnChance());
        put(Symbol.DIAMOND, Symbol.DIAMOND.baseSpawnChance());
        put(Symbol.SEVEN, Symbol.SEVEN.baseSpawnChance());
    }};

    public Symbol randomSymbolWithSpawnChance() {
        double totalWeight = symbolSpawnChanceMap.values()
            .stream()
            .mapToDouble(Float::doubleValue)
            .sum();

        double random = Math.random() * totalWeight;
        double cumulative = 0.0;
        for (Map.Entry<Symbol, Float> entry : symbolSpawnChanceMap.entrySet()) {
            cumulative += entry.getValue();
            if (random <= cumulative) {
                return entry.getKey();
            }
        }

        // Should never reach here if weights > 0
        return Symbol.LEMON;
    }
}
