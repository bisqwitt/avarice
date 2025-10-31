package com.avaricious.upgrades;

public enum UpgradeRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY;

    public UpgradeRarity getNext() {
        return values()[this.ordinal() + 1];
    }
}
