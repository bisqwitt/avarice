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
}
