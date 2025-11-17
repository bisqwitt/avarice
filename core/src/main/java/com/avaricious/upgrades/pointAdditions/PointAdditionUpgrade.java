package com.avaricious.upgrades.pointAdditions;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradeRarity;

import java.util.List;

public abstract class PointAdditionUpgrade extends Upgrade {

    public PointAdditionUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    public abstract boolean condition(List<Symbol> selection, long count);

    public abstract int getPoints();

}
