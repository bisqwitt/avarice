package com.avaricious.stats;

import com.avaricious.upgrades.UpgradeRarity;
import com.badlogic.gdx.math.MathUtils;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record StatUpgrade(Stat stat, BigDecimal percentage) {
   public static StatUpgrade newRandom() {
        if (MathUtils.random(1, 10) != 1) {
            return null;
        }

        Reflections reflections = new Reflections("com.avaricious.stats");
        List<Class<? extends Stat>> upgradeClasses = new ArrayList<>(reflections.getSubTypesOf(Stat.class));

        Stat stat;
        try {
            stat = upgradeClasses.get(MathUtils.random(upgradeClasses.size() - 1)).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                    NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new StatUpgrade(stat, new BigDecimal("0.05"));
    }
}
