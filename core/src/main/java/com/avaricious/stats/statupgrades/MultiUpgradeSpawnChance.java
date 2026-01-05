package com.avaricious.stats.statupgrades;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;

public class MultiUpgradeSpawnChance extends Stat {
    @Override
    public Texture getTexture() {
        return Assets.I().getMultiStat();
    }

    @Override
    public Texture getShadowTexture() {
        return Assets.I().getMultiStatShadow();
    }
}
