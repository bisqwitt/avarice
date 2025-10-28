package com.avaricious.upgrades.symbol.spawnChance;

import com.avaricious.slot.Symbol;
import com.avaricious.slot.SymbolManager;
import com.avaricious.upgrades.Upgrade;

public abstract class SymbolSpawnChanceUpgrade implements Upgrade {

    private final Symbol type;
    private final float amount;

    protected SymbolSpawnChanceUpgrade(Symbol symbol, boolean positive) {
        this.type = symbol;
        this.amount = positive ? 8 : -(SymbolManager.I().getSymbolSpawnChance(type) / 2);
    }

    @Override
    public void apply() {
        SymbolManager.I().changeChance(type, amount);
    }

    @Override
    public String description() {
        if(amount > 0) {
            return "Increase " + type.toString() + "'s spawn chance by " + amount + "%";
        } else {
            return "Halve " + type.toString() + "'s spawn chance";
        }
    }
}
