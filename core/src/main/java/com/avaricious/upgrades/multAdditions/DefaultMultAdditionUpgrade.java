package com.avaricious.upgrades.multAdditions;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;

import java.util.List;
import java.util.Map;

public class DefaultMultAdditionUpgrade extends MultAdditionUpgrade {

    private final Map<UpgradeRarity, Integer> valuePerRarityMap = Map.of(
        UpgradeRarity.COMMON, 2,
        UpgradeRarity.UNCOMMON, 3,
        UpgradeRarity.RARE, 4,
        UpgradeRarity.EPIC, 5,
        UpgradeRarity.LEGENDARY, 7
    );

    public DefaultMultAdditionUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return true;
    }

    @Override
    public int getMulti() {
        return valuePerRarityMap.get(getRarity());
    }

    @Override
    public String description() {
        return "Add " + getMulti() + " to multiplier";
    }

    @Override
    public void apply() {
    }
}
