package com.avaricious.upgrades.multAdditions;

import com.avaricious.RoundsManager;
import com.avaricious.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;

import java.util.List;
import java.util.Map;

public class OnLastHandMultAdditionUpgrade extends MultAdditionUpgrade {

    private final Map<UpgradeRarity, Integer> valuePerRarityMap = Map.of(
        UpgradeRarity.COMMON, 5,
        UpgradeRarity.UNCOMMON, 7,
        UpgradeRarity.RARE, 10,
        UpgradeRarity.EPIC, 12,
        UpgradeRarity.LEGENDARY, 15
    );

    public OnLastHandMultAdditionUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return RoundsManager.I().getHandsLeft() == 1;
    }

    @Override
    public int getMulti() {
        return valuePerRarityMap.get(getRarity());
    }

    @Override
    public String description() {
        return "Add " + getMulti() + " to multiplier on final hand";
    }
}
