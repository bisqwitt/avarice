package com.avaricious.upgrades.pointAdditions;

import com.avaricious.RoundsManager;
import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;

import java.util.List;
import java.util.Map;

public class PointsForEachRemainingSpinUpgrade extends PointAdditionUpgrade {

    private final Map<UpgradeRarity, Integer> valuePerRarityMap = Map.of(
        UpgradeRarity.COMMON, 3,
        UpgradeRarity.UNCOMMON, 5,
        UpgradeRarity.RARE, 7,
        UpgradeRarity.EPIC, 10,
        UpgradeRarity.LEGENDARY, 15
    );

    public PointsForEachRemainingSpinUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return true;
    }

    @Override
    public int getPoints() {
        return RoundsManager.I().getSpinsLeft() * pointsPerSpin();
    }

    private int pointsPerSpin() {
        return valuePerRarityMap.get(getRarity());
    }

    @Override
    public String description() {
        return "+" + pointsPerSpin() + " Points for each remaining spin";
    }
}
