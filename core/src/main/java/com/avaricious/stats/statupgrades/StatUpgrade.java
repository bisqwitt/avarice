package com.avaricious.stats.statupgrades;

import com.avaricious.stats.PlayerStats;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradeRarity;
import com.badlogic.gdx.math.MathUtils;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StatUpgrade extends Upgrade {

   public static StatUpgrade newRandom() {
//        if (MathUtils.random(1, 20) != 1) {
//            return null;
//        }

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

   private final Stat stat;
   private final BigDecimal percentage;

   private StatUpgrade(Stat stat, BigDecimal percentage) {
       super(UpgradeRarity.COMMON);
       this.stat = stat;
      this.percentage = percentage;
   }

    public Stat getStat() {
        return stat;
    }

    @Override
    public String description() {
        return "Increase " + stat.getClass().getSimpleName() + " by " + (percentage.floatValue() * 100) + "%";
    }

    @Override
    public void apply() {
       Stat playerStat = PlayerStats.I().getStat(stat.getClass());
       playerStat.setPercentageChance(playerStat.getPercentageChance().add(percentage));
    }
}
