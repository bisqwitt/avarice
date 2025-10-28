package com.avaricious.upgrades;

import com.avaricious.slot.Symbol;
import com.avaricious.upgrades.chipAdditions.ChipAdditionUpgrade;
import com.avaricious.upgrades.multAdditions.MultAdditionUpgrade;
import com.avaricious.upgrades.symbol.spawnChance.SymbolSpawnChanceUpgrade;
import com.badlogic.gdx.Gdx;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public class UpgradesManager {

    private static UpgradesManager instance;
    public static UpgradesManager I() {
        return instance == null ? instance = new UpgradesManager() : instance;
    }

    private final List<Upgrade> allUpgrades = new ArrayList<>();
    private final List<Upgrade> appliedUpgrades = new ArrayList<>();

    public long upgradeMultAdditions(List<Symbol> selection, long count) {
        return appliedUpgrades.stream()
            .filter(upgrade -> upgrade instanceof MultAdditionUpgrade)
            .map(upgrade -> (MultAdditionUpgrade) upgrade)
            .filter(upgrade -> upgrade.condition(selection, count))
            .mapToLong(MultAdditionUpgrade::getMulti)
            .sum();
    }

    public long upgradeChipAdditions(List<Symbol> selection, long count) {
        return appliedUpgrades.stream()
            .filter(upgrade -> upgrade instanceof ChipAdditionUpgrade)
            .map(upgrade -> (ChipAdditionUpgrade) upgrade)
            .filter(upgrade -> upgrade.condition(selection, count))
            .mapToLong(ChipAdditionUpgrade::getChips)
            .sum();
    }

    private UpgradesManager() {
        Reflections reflections = new Reflections("com.avaricious.upgrades");
        Set<Class<? extends Upgrade>> upgradeClasses = reflections.getSubTypesOf(Upgrade.class);

        upgradeClasses.stream()
            .filter(c -> !c.isInterface())
            .filter(c -> !Modifier.isAbstract(c.getModifiers()))
            .filter(c -> !SymbolSpawnChanceUpgrade.class.isAssignableFrom(c))
            .forEach(upgradeClass -> {
                try {
                    allUpgrades.add(upgradeClass.getDeclaredConstructor().newInstance());
                    Gdx.app.log("UPGRADE: REGISTER", upgradeClass.getName());
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    Gdx.app.error("UPGRADE: INSTANCE ERROR", e.getMessage());
                }
            });
    }

    public List<Upgrade> randomUpgrades() {
        Collections.shuffle(allUpgrades);
        return allUpgrades.subList(0, 4);
    }

    public void applyUpgrade(Upgrade upgrade) {
        upgrade.apply();
        appliedUpgrades.add(upgrade);
    }

    public List<Upgrade> getAppliedUpgrades() {
        return appliedUpgrades;
    }
}
