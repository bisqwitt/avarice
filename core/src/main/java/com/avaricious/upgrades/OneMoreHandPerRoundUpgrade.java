package com.avaricious.upgrades;

import com.avaricious.RoundsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OneMoreHandPerRoundUpgrade extends Upgrade {

    private final Map<UpgradeRarity, Integer> valuePerRarityMap = Map.of(
        UpgradeRarity.COMMON, 1,
        UpgradeRarity.UNCOMMON, 1,
        UpgradeRarity.RARE, 2,
        UpgradeRarity.EPIC, 3,
        UpgradeRarity.LEGENDARY, 4
    );

    public OneMoreHandPerRoundUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    public int getAmount() {
        return valuePerRarityMap.get(getRarity());
    }

    @Override
    public String description() {
        return "+" + getAmount() + (getAmount() != 1 ? "Hand's" : " Hand") + " per Round";
    }
}
