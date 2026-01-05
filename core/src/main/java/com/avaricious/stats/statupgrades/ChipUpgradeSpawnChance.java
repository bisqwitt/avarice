package com.avaricious.stats.statupgrades;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;

public class ChipUpgradeSpawnChance extends Stat {
    @Override
    public Texture getTexture() {
        return Assets.I().getPokerChip();
    }

    @Override
    public Texture getShadowTexture() {
        return Assets.I().getPokerChipShadow();
    }
}
