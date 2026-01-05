package com.avaricious.stats.statupgrades;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;

public class DoubleHitChance extends Stat {
    @Override
    public Texture getTexture() {
        return Assets.I().getRetriggerStat();
    }

    @Override
    public Texture getShadowTexture() {
        return Assets.I().getRetriggerShadow();
    }
}
