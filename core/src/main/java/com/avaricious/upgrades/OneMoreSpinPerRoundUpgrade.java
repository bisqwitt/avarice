package com.avaricious.upgrades;

import com.avaricious.RoundsManager;

public class OneMoreSpinPerRoundUpgrade implements Upgrade{
    @Override
    public void apply() {
        RoundsManager roundsManager = RoundsManager.I();
        roundsManager.setSpinsPerRound(roundsManager.getSpinsPerRound() + 1);
    }

    @Override
    public String description() {
        return "Play one more Spin per Round";
    }
}
