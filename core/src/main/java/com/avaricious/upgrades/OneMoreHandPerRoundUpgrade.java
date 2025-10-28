package com.avaricious.upgrades;

import com.avaricious.RoundsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneMoreHandPerRoundUpgrade implements Upgrade {
    @Override
    public void apply() {
        RoundsManager roundsManager = RoundsManager.I();
        roundsManager.setHandsPerRound(roundsManager.getHandsPerRound() + 1);
    }

    @Override
    public String description() {
        return "One more Hand per Round";
    }
}
