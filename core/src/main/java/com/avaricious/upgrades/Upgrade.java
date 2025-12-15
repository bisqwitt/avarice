package com.avaricious.upgrades;

import java.util.List;

public abstract class Upgrade {

    private UpgradeRarity rarity;

    public Upgrade(UpgradeRarity rarity) {
        this.rarity = rarity;
    }

    public abstract String description();

    public UpgradeRarity getRarity() {
        return rarity;
    }

    public abstract void apply();

    public void increaseRarity() {
        rarity = rarity.getNext();
    }

    public int getPrice() {
        switch (rarity) {
            case COMMON -> {
                return 3;
            }
            case UNCOMMON -> {
                return 6;
            }
            case RARE -> {
                return 12;
            }
            case EPIC -> {
                return 24;
            }
            case LEGENDARY -> {
                return 48;
            }
            default -> {
                return 0;
            }
        }
    }
}
