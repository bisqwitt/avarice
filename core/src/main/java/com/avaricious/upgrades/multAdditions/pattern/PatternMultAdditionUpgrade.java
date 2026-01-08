package com.avaricious.upgrades.multAdditions.pattern;

import com.avaricious.Assets;
import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;
import com.avaricious.upgrades.multAdditions.MultAdditionUpgrade;

import java.util.List;
import java.util.Map;

public abstract class PatternMultAdditionUpgrade extends MultAdditionUpgrade {

    private final Map<UpgradeRarity, Integer> valuePerRarityMap = Map.of(
        UpgradeRarity.COMMON, 4,
        UpgradeRarity.UNCOMMON, 5,
        UpgradeRarity.RARE, 6,
        UpgradeRarity.EPIC, 7,
        UpgradeRarity.LEGENDARY, 9
    );

    private final double patternType;

    protected PatternMultAdditionUpgrade(UpgradeRarity rarity, long patternType) {
        super(rarity);
        this.patternType = patternType;
    }

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return count == patternType;
    }

    @Override
    public int getMulti() {
        return valuePerRarityMap.get(getRarity());
    }

    @Override
    public void apply() {
    }

    @Override
    public String description() {
        return "Additional " + Assets.I().redText(getMulti() + "") + " to " + Assets.I().redText("multiplier") + " on " + (int) patternType + "-of-a-kind's";
    }
}
