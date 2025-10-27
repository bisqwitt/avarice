package com.avaricious.upgrades.symbol;

import com.avaricious.symbol.Symbol;
import com.avaricious.slot.SymbolManager;
import com.avaricious.upgrades.Upgrade;

public class SymbolSpawnChanceUpgrade implements Upgrade {

    private final Symbol type;
    private final float amount;

    SymbolSpawnChanceUpgrade(Symbol type, float amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public void apply() {
        SymbolManager.I().changeChance(type, amount);
    }

    @Override
    public String description() {
        return (amount > 0 ? "Increase " : "Decrease ") + type.toString() + "'s spawn chance by " + amount + "%";
    }
}
