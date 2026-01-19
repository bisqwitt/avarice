package com.avaricious.stats.statupgrades;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;

public class OmnivampAmount extends Stat {
    @Override
    public Texture getTexture() {
        return Assets.I().getOmnivampStat();
    }

    @Override
    public Texture getShadowTexture() {
        return Assets.I().getOmnivampStatShadow();
    }
}
