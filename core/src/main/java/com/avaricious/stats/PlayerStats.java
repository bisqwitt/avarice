package com.avaricious.stats;

import com.avaricious.stats.statupgrades.*;

import java.math.BigInteger;
import java.util.Map;

public class PlayerStats {

    private static PlayerStats instance;

    public static PlayerStats I() {
        return instance == null ? instance = new PlayerStats() : instance;
    }

    private final Map<Class<? extends Stat>, Stat> stats = Map.of(
        ChipUpgradeSpawnChance.class, new ChipUpgradeSpawnChance(),
        CreditSpawnChance.class, new CreditSpawnChance(),
        CritChance.class, new CritChance(),
        DoubleHitChance.class, new DoubleHitChance(),
        EvadeChance.class, new EvadeChance(),
        LuckChance.class, new LuckChance(),
        MultiUpgradeSpawnChance.class, new MultiUpgradeSpawnChance()
    );

    private PlayerStats() {
    }

    public boolean rollChance(Class<? extends Stat> statClass) {
        return stats.get(statClass).rollChance();
    }

    public Stat getStat(Class<? extends Stat> statClass) {
        return stats.get(statClass);
    }

}
