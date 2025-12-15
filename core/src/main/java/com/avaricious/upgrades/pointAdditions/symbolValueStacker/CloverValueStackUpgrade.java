package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;

public class CloverValueStackUpgrade extends SymbolValueStackUpgrade {
    public CloverValueStackUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    @Override
    public Symbol getSymbol() {
        return Symbol.CLOVER;
    }
}
