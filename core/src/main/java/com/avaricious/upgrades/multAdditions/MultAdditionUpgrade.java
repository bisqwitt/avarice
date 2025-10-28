package com.avaricious.upgrades.multAdditions;

import com.avaricious.slot.Symbol;
import com.avaricious.upgrades.Upgrade;

import java.util.List;

public abstract class MultAdditionUpgrade implements Upgrade {

    protected MultAdditionUpgrade() {}

    protected boolean applied = false;

    public abstract boolean condition(List<Symbol> selection, long count);

    public abstract long getMulti();

    @Override
    public void apply() {
        applied = true;
    }
}
