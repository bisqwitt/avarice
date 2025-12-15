package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;

public class IronValueStackUpgrade extends SymbolValueStackUpgrade {
    public IronValueStackUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    @Override
    public Symbol getSymbol() {
        return Symbol.IRON;
    }
}
