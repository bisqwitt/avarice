package com.avaricious.stats.statupgrades;

import com.avaricious.stats.PlayerStats;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradeRarity;

import java.math.BigDecimal;

public class StatUpgrade extends Upgrade {

   public static StatUpgrade newRandom() {
//        if (MathUtils.random(1, 20) != 1) {
//            return null;
//        }
       return new StatUpgrade(PlayerStats.I().getRandomStat(), new BigDecimal("0.05"));
   }

   private final Stat stat;
   private final BigDecimal additionalPercentage;

   private StatUpgrade(Stat stat, BigDecimal percentage) {
       super(UpgradeRarity.COMMON);
       this.stat = stat;
      this.additionalPercentage = percentage;
   }

    public Stat getStat() {
        return stat;
    }

    @Override
    public String description() {
        return "Increase " + stat.getClass().getSimpleName() + " by " + (additionalPercentage.floatValue() * 100) + "%";
    }

    @Override
    public void apply() {
       stat.setPercentage(stat.getPercentage().add(additionalPercentage));
    }

    public int getAdditionalPercentage() {
        return (int) (additionalPercentage.floatValue() * 100);
    }
}
