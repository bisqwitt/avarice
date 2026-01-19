package com.avaricious.upgrades.multAdditions;

import com.avaricious.Assets;
import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;
import com.avaricious.upgrades.UpgradesManager;

import java.util.List;

public class MultPerEmptyJokerSlotUpgrade extends MultAdditionUpgrade {


    public MultPerEmptyJokerSlotUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return true;
    }

    @Override
    public int getMulti() {
        return (6 - UpgradesManager.I().getDeck().size()) * 5;
    }

    @Override
    public String description() {
        return Assets.I().redText("+5 mult") + " for each empty Joker slot\n"
            + Assets.I().greenText("(Currently +" + getMulti() + ")");
    }

    @Override
    public void apply() {
    }
}
