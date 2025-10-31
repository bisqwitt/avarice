package com.avaricious.upgrades;

import com.avaricious.RoundsManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OneMoreSpinPerRoundUpgrade extends Upgrade {

    private final Map<UpgradeRarity, Integer> valuePerRarityMap = Map.of(
        UpgradeRarity.COMMON, 1,
        UpgradeRarity.UNCOMMON, 2,
        UpgradeRarity.RARE, 3,
        UpgradeRarity.EPIC, 4,
        UpgradeRarity.LEGENDARY, 5
    );

    public OneMoreSpinPerRoundUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    public int getAmount() {
        return valuePerRarityMap.get(getRarity());
    }

    @Override
    public String description() {
        return "+" + getAmount() + (getAmount() != 1 ? " Spin's" : " Spin") + " per Round";
    }
}
