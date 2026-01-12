package com.avaricious.upgrades;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.pointAdditions.PointAdditionUpgrade;
import com.avaricious.upgrades.multAdditions.MultAdditionUpgrade;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpgradesManager {

    private static UpgradesManager instance;
    public static UpgradesManager I() {
        return instance == null ? instance = new UpgradesManager() : instance;
    }

    private UpgradesManager() {
        Reflections reflections = new Reflections("com.avaricious.upgrades");
        Set<Class<? extends Upgrade>> upgradeClasses = reflections.getSubTypesOf(Upgrade.class);

        allUpgrades.addAll(upgradeClasses.stream()
            .filter(c -> !c.isInterface())
            .filter(c -> !Modifier.isAbstract(c.getModifiers()))
            .collect(Collectors.toSet()));

        randomUpgrades().forEach(this::addUpgrade);
        randomUpgrades().forEach(this::addUpgrade);
    }

    private final List<Class<? extends Upgrade>> allUpgrades = new ArrayList<>();
    private final List<Upgrade> deck = new ArrayList<>();

    public int multAdditions(List<Symbol> selection, long count) {
        return deck.stream()
            .filter(MultAdditionUpgrade.class::isInstance)
            .map(MultAdditionUpgrade.class::cast)
            .filter(upgrade -> upgrade.condition(selection, count))
            .mapToInt(MultAdditionUpgrade::getMulti)
            .sum();
    }

    public int chipAdditions(List<Symbol> selection, long count) {
        return deck.stream()
            .filter(PointAdditionUpgrade.class::isInstance)
            .map(PointAdditionUpgrade.class::cast)
            .filter(upgrade -> upgrade.condition(selection, count))
            .mapToInt(PointAdditionUpgrade::getPoints)
            .sum();
    }

    public List<? extends Upgrade> randomUpgrades() {
        List<Class<? extends Upgrade>> randomUpgrades = List.of(
            allUpgrades.get((int) (Math.random() * allUpgrades.size())),
            allUpgrades.get((int) (Math.random() * allUpgrades.size())),
            allUpgrades.get((int) (Math.random() * allUpgrades.size()))
        );
        return randomUpgrades.stream().map(upgradeClass -> {
            try {
                return upgradeClass.getDeclaredConstructor(UpgradeRarity.class).newInstance(UpgradeRarity.COMMON);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public <T> Stream<T> getUpgradesOfClass(Class<T> clazz) {
        return getUpgrades().stream()
            .filter(clazz::isInstance)
            .map(clazz::cast);
    }

    public void addUpgrade(Upgrade upgrade) {
        deck.add(upgrade);
        mergeDuplicates();
    }

    public List<Upgrade> getUpgrades() {
        return deck;
    }

    private void mergeDuplicates() {
        record Key(Class<?> type, UpgradeRarity rarity) {}

        var dupKey = deck.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                u -> new Key(u.getClass(), u.getRarity())
            ))
            .entrySet().stream()
            .filter(e -> e.getValue().size() >= 2)
            .map(java.util.Map.Entry::getKey)
            .findFirst();

        if (dupKey.isEmpty()) return;

        var key = dupKey.get();
        var pair = deck.stream()
            .filter(u -> u.getClass() == key.type() && u.getRarity() == key.rarity())
            .limit(2)
            .toList();

        if (pair.get(0).getRarity() == UpgradeRarity.LEGENDARY) {
            return;
        }

        deck.remove(pair.get(1));
        pair.get(0).increaseRarity();

        mergeDuplicates();
    }

    public void removeUpgrade(Upgrade upgrade) {
        deck.remove(upgrade);
    }

    public List<Upgrade> getDeck() {
        return deck;
    }

    public boolean spaceInDeck() {
        return deck.size() < 7;
    }
}
