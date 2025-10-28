package com.avaricious;

import java.util.HashMap;
import java.util.Map;

public class RoundsManager {

    private static RoundsManager instance;
    public static RoundsManager I() {
        return instance == null ? (instance = new RoundsManager()) : instance;
    }

    private RoundsManager() {
        currentRound = 0;
        currentTargetScore = targetScorePerRound.get(currentRound);

        handsPerRound = 4;
        spinsPerRound = 4;

        handsLeft = handsPerRound;
        spinsLeft = spinsPerRound;
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

    private Integer handsPerRound;
    private Integer spinsPerRound;

    public void nextRound() {
        currentRound++;
        currentTargetScore = targetScorePerRound.get(currentRound);

        handsLeft = handsPerRound;
        spinsLeft = spinsPerRound;
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

    public Integer getHandsLeft() {
        return handsLeft;
    }

    public Integer getSpinsLeft() {
        return spinsLeft;
    }

    public void setHandsPerRound(Integer handsPerRound) {
        this.handsPerRound = handsPerRound;
    }

    public void setSpinsPerRound(Integer spinsPerRound) {
        this.spinsPerRound = spinsPerRound;
    }

    public Integer getHandsPerRound() {
        return handsPerRound;
    }

    public Integer getSpinsPerRound() {
        return spinsPerRound;
    }
}
