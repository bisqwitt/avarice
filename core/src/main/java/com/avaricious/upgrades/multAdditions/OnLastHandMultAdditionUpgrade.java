package com.avaricious.upgrades.multAdditions;

import com.avaricious.RoundsManager;
import com.avaricious.slot.Symbol;

import java.util.List;

public class OnLastHandMultAdditionUpgrade extends MultAdditionUpgrade {
    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return applied && RoundsManager.I().getHandsLeft() == 1;
    }

    @Override
    public long getMulti() {
        return 12;
    }

    @Override
    public String description() {
        return "Add 12 to multiplier on final hand";
    }
}
