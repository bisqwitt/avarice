package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;

public class BellValueStackUpgrade extends SymbolValueStackUpgrade {
    public BellValueStackUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    @Override
    public Symbol getSymbol() {
        return Symbol.BELL;
    }
}
