package com.avaricious.upgrades;

import com.badlogic.gdx.Gdx;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class UpgradesManager {

    private static UpgradesManager instance;
    public static UpgradesManager getInstance() {
        return instance == null ? instance = new UpgradesManager() : instance;
    }

    private final List<Upgrade> upgrades = new ArrayList<>();

    private UpgradesManager() {}

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    public void addNew(Class<? extends Upgrade> upgradeClass) {
        try {
            upgrades.add(upgradeClass.getDeclaredConstructor().newInstance());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Gdx.app.error(UpgradesManager.class.getCanonicalName(), e.getMessage());
        }
    }

}
