package com.avaricious.upgrades;

import com.avaricious.Assets;

public class DeptUpgrade extends Upgrade {

    public DeptUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    @Override
    public String description() {
        return "Go up to " + Assets.I().yellowText("-20$") + " in dept";
    }

    @Override
    public void apply() {

    }
}
