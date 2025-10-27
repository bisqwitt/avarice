package com.avaricious;

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
        put(1, 100L);
        put(2, 150L);
        put(3, 200L);
        put(4, 300L);
        put(5, 450L);
        put(6, 600L);
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
