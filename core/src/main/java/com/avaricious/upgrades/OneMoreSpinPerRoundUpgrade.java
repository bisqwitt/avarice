package com.avaricious.upgrades;

import com.avaricious.RoundsManager;

import java.util.Arrays;
import java.util.List;

public class OneMoreSpinPerRoundUpgrade implements Upgrade{
    @Override
    public void apply() {
        RoundsManager roundsManager = RoundsManager.I();
        roundsManager.setSpinsPerRound(roundsManager.getSpinsPerRound() + 1);
    }

    @Override
    public String description() {
        return "One more Spin per Round";
    }
}
