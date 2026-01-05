package com.avaricious.stats.statupgrades;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;

public class LuckChance extends Stat {
    @Override
    public Texture getTexture() {
        return Assets.I().getLuckStat();
    }

    @Override
    public Texture getShadowTexture() {
        return Assets.I().getLuckStatShadow();
    }
}
