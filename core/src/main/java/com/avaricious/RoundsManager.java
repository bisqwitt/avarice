package com.avaricious;

import com.avaricious.upgrades.UpgradesManager;

import java.util.HashMap;
import java.util.Map;

public class RoundsManager {

    private static RoundsManager instance;
    public static RoundsManager I() {
        return instance == null ? (instance = new RoundsManager()) : instance;
    }

    private RoundsManager() {
        currentRound = 1;
        currentTargetScore = targetScorePerRound.get(currentRound);
    }

    private final Map<Integer, Long> targetScorePerRound = new HashMap<Integer, Long>() {{
        put(0, 1L);

        put(1, 300L);
        put(2, 450L);
        put(3, 600L);

        put(4, 800L);
        put(5, 1200L);
        put(6, 1600L);

        put(7, 2000L);
        put(8, 3000L);
        put(9, 4000L);

        put(10, 5000L);
        put(11, 7500L);
        put(12, 10000L);
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
