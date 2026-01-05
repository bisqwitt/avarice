package com.avaricious.stats.statupgrades;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;

public class EvadeChance extends Stat {
    @Override
    public Texture getTexture() {
        return Assets.I().getEvadeStat();
    }

    @Override
    public Texture getShadowTexture() {
        return Assets.I().getEvadeStatShadow();
    }
}
