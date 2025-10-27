package com.avaricious.slot;

import com.avaricious.symbol.Symbol;

import java.util.HashMap;
import java.util.Map;

public class SymbolManager {

    private static SymbolManager instance;
    public static SymbolManager I() {
        return instance ==  null ? instance = new SymbolManager() : instance;
    }

    private SymbolManager() {}

    private Map<Symbol, Integer> symbolValueMap = new HashMap<Symbol, Integer>() {{
        put(Symbol.LEMON, Symbol.LEMON.baseValue());
        put(Symbol.CHERRY, Symbol.CHERRY.baseValue());
        put(Symbol.CLOVER, Symbol.CLOVER.baseValue());
        put(Symbol.BELL, Symbol.BELL.baseValue());
        put(Symbol.IRON, Symbol.IRON.baseValue());
        put(Symbol.DIAMOND, Symbol.DIAMOND.baseValue());
        put(Symbol.SEVEN, Symbol.SEVEN.baseValue());
    }};

    private final Map<Symbol, Float> symbolSpawnChanceMap = new HashMap<Symbol, Float>() {{
        put(Symbol.LEMON, Symbol.LEMON.baseSpawnChance());
        put(Symbol.CHERRY, Symbol.CHERRY.baseSpawnChance());
        put(Symbol.CLOVER, Symbol.CLOVER.baseSpawnChance());
        put(Symbol.BELL, Symbol.BELL.baseSpawnChance());
        put(Symbol.IRON, Symbol.IRON.baseSpawnChance());
        put(Symbol.DIAMOND, Symbol.DIAMOND.baseSpawnChance());
        put(Symbol.SEVEN, Symbol.SEVEN.baseSpawnChance());
    }};

    public void changeValue(Symbol symbol, Integer amount) {
        symbolValueMap.put(symbol, symbolValueMap.get(symbol) + amount);
    }

    public void changeChance(Symbol symbol, float amount) {
        symbolSpawnChanceMap.put(symbol, symbolSpawnChanceMap.get(symbol) + amount);

        float balancing = amount / 6;
        symbolSpawnChanceMap.keySet()
            .stream().filter((s) -> !s.equals(symbol))
            .forEach((s) -> symbolSpawnChanceMap.put(s, symbolSpawnChanceMap.get(s) - balancing));
    }

    public Symbol randomSymbol() {
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
