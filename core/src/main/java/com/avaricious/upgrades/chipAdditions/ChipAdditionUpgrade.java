package com.avaricious.upgrades.chipAdditions;

import com.avaricious.slot.Symbol;
import com.avaricious.upgrades.Upgrade;

import java.util.List;

public abstract class ChipAdditionUpgrade implements Upgrade {

    protected boolean applied = false;

    protected ChipAdditionUpgrade() {}

    public abstract boolean condition(List<Symbol> selection, long count);

    public abstract long getChips();

    @Override
    public void apply() {
        applied = true;
    }
}
