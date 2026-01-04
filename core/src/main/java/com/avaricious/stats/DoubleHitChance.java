package com.avaricious.stats;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;

public class DoubleHitChance extends Stat {
    @Override
    public Texture getTexture() {
        return Assets.I().getRetriggerStat();
    }
}
