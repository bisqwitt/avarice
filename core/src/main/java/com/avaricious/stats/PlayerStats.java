package com.avaricious.stats;

import com.avaricious.stats.statupgrades.*;
import com.badlogic.gdx.math.MathUtils;

import java.util.List;
import java.util.Map;

public class PlayerStats {

    private static PlayerStats instance;

    public static PlayerStats I() {
        return instance == null ? instance = new PlayerStats() : instance;
    }

    private final Map<Class<? extends Stat>, Stat> stats = Map.of(
        CreditSpawnChance.class, new CreditSpawnChance(),
        CriticalHitChance.class, new CriticalHitChance(),
        DoubleHitChance.class, new DoubleHitChance(),
        EvadeChance.class, new EvadeChance(),
        LuckChance.class, new LuckChance(),
        Omnivamp.class, new Omnivamp()
    );

    private PlayerStats() {
    }

    public boolean rollChance(Class<? extends Stat> statClass) {
        return stats.get(statClass).rollChance();
    }

    public Stat getStat(Class<? extends Stat> statClass) {
        return stats.get(statClass);
    }

    public Stat getRandomStat() {
        List<Stat> values = stats.values().stream().toList();
        return values.get(MathUtils.random(0, values.size() -1));
    }

}
