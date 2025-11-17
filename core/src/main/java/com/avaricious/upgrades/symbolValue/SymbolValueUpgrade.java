package com.avaricious.upgrades.symbolValue;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradeRarity;

import java.util.Map;

public abstract class SymbolValueUpgrade extends Upgrade {

    private final Symbol symbol;

    private final Map<UpgradeRarity, Integer> valuePerRarityMap = Map.of(
        UpgradeRarity.COMMON, 3,
        UpgradeRarity.UNCOMMON, 5,
        UpgradeRarity.RARE, 7,
        UpgradeRarity.EPIC, 10,
        UpgradeRarity.LEGENDARY, 15
    );

    protected SymbolValueUpgrade(UpgradeRarity rarity, Symbol symbol) {
        super(rarity);
        this.symbol = symbol;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Integer getAmount() {
        return valuePerRarityMap.get(getRarity());
    }

    @Override
    public String description() {
        return "Increase " + symbol.toString() + "'s value by " + getAmount();
    }

}
