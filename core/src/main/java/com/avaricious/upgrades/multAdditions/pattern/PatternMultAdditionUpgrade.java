package com.avaricious.upgrades.multAdditions.pattern;

import com.avaricious.slot.Symbol;
import com.avaricious.upgrades.multAdditions.MultAdditionUpgrade;

import java.util.List;

public abstract class PatternMultAdditionUpgrade extends MultAdditionUpgrade {

    private final double patternType;

    protected PatternMultAdditionUpgrade(long patternType) {
        this.patternType = patternType;
    }

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return applied && count == patternType;
    }

    @Override
    public long getMulti() {
        return 8;
    }

    @Override
    public String description() {
        return "Add 8 to multiplier on " + (int) patternType + "-of-a-kind's";
    }
}
