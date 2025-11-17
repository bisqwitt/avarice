package com.avaricious.upgrades.multAdditions;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradeRarity;

import java.util.List;

public abstract class MultAdditionUpgrade extends Upgrade {

    public MultAdditionUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    public abstract boolean condition(List<Symbol> selection, long count);

    public abstract int getMulti();

}
