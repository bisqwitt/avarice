package com.avaricious.stats.statupgrades;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;

public class CriticalHitChance extends Stat {
    @Override
    public Texture getTexture() {
        return Assets.I().getCriticalHitStat();
    }

    @Override
    public Texture getShadowTexture() {
        return Assets.I().getCriticalHitStatShadow();
    }
}
