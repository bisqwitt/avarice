package com.avaricious.stats.statupgrades;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;

public class CreditSpawnChance extends Stat {
    @Override
    public Texture getTexture() {
        return Assets.I().getCoinStat();
    }

    @Override
    public Texture getShadowTexture() {
        return Assets.I().getCoinStatShadow();
    }
}
