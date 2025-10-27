package com.avaricious.upgrades;

import com.avaricious.RoundsManager;

public class OneMoreHandPerRoundUpgrade implements Upgrade {
    @Override
    public void apply() {
        RoundsManager roundsManager = RoundsManager.I();
        roundsManager.setHandsPerRound(roundsManager.getHandsPerRound() + 1);
    }

    @Override
    public String description() {
        return "Play one more Hand per Round";
    }
}
