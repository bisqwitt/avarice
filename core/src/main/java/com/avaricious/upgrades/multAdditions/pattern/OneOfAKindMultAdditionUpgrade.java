package com.avaricious.upgrades.multAdditions.pattern;

import com.avaricious.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;
import com.avaricious.upgrades.multAdditions.MultAdditionUpgrade;

import java.util.List;

public class OneOfAKindMultAdditionUpgrade extends PatternMultAdditionUpgrade {

    public OneOfAKindMultAdditionUpgrade(UpgradeRarity rarity) {
        super(rarity, 1L);
    }
}
