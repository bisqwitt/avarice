package com.avaricious.upgrades;

import com.badlogic.gdx.Gdx;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class UpgradesManager {

    private static UpgradesManager instance;
    public static UpgradesManager I() {
        return instance == null ? instance = new UpgradesManager() : instance;
    }

    private final List<Upgrade> allUpgrades = new ArrayList<>();
    private final List<Upgrade> appliedUpgrades = new ArrayList<>();

    private UpgradesManager() {
        Reflections reflections = new Reflections("com.avaricious.upgrades");
        Set<Class<? extends Upgrade>> upgradeClasses = reflections.getSubTypesOf(Upgrade.class);

        upgradeClasses.forEach(upgradeClass -> {
            try {
                allUpgrades.add(upgradeClass.getDeclaredConstructor().newInstance());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Gdx.app.error("UPGRADE INSTANCE ERROR", e.getMessage());
            }
        });
    }

    public List<Upgrade> randomUpgrades() {
        Collections.shuffle(allUpgrades);
        return allUpgrades.subList(0, 3);
    }

    public void applyUpgrade(Upgrade upgrade) {
        upgrade.apply();
        appliedUpgrades.add(upgrade);
    }

    public List<Upgrade> getAppliedUpgrades() {
        return appliedUpgrades;
    }
}
