package com.avaricious.upgrades.multAdditions;

import com.avaricious.slot.Symbol;
import com.avaricious.upgrades.Upgrade;

import java.util.List;

public class DefaultMultAdditionUpgrade extends MultAdditionUpgrade {

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return applied;
    }

    @Override
    public long getMulti() {
        return 4;
    }

    @Override
    public String description() {
        return "Add 4 to multiplier";
    }
}
