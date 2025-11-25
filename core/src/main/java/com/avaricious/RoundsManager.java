package com.avaricious;

import com.avaricious.upgrades.UpgradesManager;

import java.util.HashMap;
import java.util.Map;

public class RoundsManager {

    private final int baseHandsPerRound = 40;
    private final int baseSpinsPerRound = 40;

    private static RoundsManager instance;
    public static RoundsManager I() {
        return instance == null ? (instance = new RoundsManager()) : instance;
    }

    private RoundsManager() {
        currentRound = 0;
        currentTargetScore = targetScorePerRound.get(currentRound);

        handsLeft = baseHandsPerRound;
        spinsLeft = baseSpinsPerRound;
    }

    private final Map<Integer, Long> targetScorePerRound = new HashMap<Integer, Long>() {{
        put(0, 1L);

        put(1, 400L);
        put(2, 600L);
        put(3, 800L);

        put(4, 1000L);
        put(5, 1500L);
        put(6, 2000L);

        put(7, 2400L);
        put(8, 3600L);
        put(9, 4800L);

        put(10, 5500L);
        put(11, 8250L);
        put(12, 11000L);
    }};

    private Integer currentRound;
    private Long currentTargetScore;

    private Integer handsLeft;
    private Integer spinsLeft;

    public void nextRound() {
        currentRound++;
        currentTargetScore = targetScorePerRound.get(currentRound);
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public Long getCurrentTargetScore() {
        return currentTargetScore;
    }

    public void minusOneHand() {
        handsLeft--;
    }

    public void minusOneSpin() {
        spinsLeft--;
    }

    public Integer getAppliesLeft() {
        return handsLeft;
    }

    public Integer getSpinsLeft() {
        return spinsLeft;
    }
}
