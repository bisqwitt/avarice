package com.avaricious.upgrades.symbol;

import com.avaricious.slot.Symbol;
import com.avaricious.slot.SymbolManager;
import com.avaricious.upgrades.Upgrade;

public class SymbolSpawnChanceUpgrade implements Upgrade {

    private final Symbol type;
    private final float amount;

    public SymbolSpawnChanceUpgrade() {
        this.type = SymbolManager.I().randomSymbolWithEqualSpawnChance();
        this.amount = Math.random() > 0.5 ? 8 : -(SymbolManager.I().getSymbolSpawnChance(type) / 2);
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
