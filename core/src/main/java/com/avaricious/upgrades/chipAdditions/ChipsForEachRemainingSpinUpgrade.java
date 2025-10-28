package com.avaricious.upgrades.chipAdditions;

import com.avaricious.RoundsManager;
import com.avaricious.slot.Symbol;

import java.util.List;

public class ChipsForEachRemainingSpinUpgrade extends ChipAdditionUpgrade {

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return applied;
    }

    @Override
    public long getChips() {
        return RoundsManager.I().getSpinsLeft() * 10L;
    }

    @Override
    public String description() {
        return "+10 Chips for each remaining spin";
    }
}
