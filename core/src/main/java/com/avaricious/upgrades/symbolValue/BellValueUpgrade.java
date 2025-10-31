package com.avaricious.upgrades.symbolValue;

import com.avaricious.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;

public class BellValueUpgrade extends SymbolValueUpgrade {
    public BellValueUpgrade(UpgradeRarity rarity) {
        super(rarity, Symbol.BELL);
    }
}
