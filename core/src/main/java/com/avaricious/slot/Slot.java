package com.avaricious.slot;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Slot {
    private float stateTime = 0f;
    private final float posX;
    private final float posY;

    public float scale = 1f;
    public float targetScale = 1f;

    private boolean wasSelected = false;
    private float pulseTime = 0f;
    private final float pulseDuration = 0.15f;
    private final float pulseAmp = 0.125f;

    public Slot(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public TextureRegion getFrame(Symbol symbol, boolean selected, float delta) {
        if (selected) stateTime += delta;
        return selected
            ? Assets.I().getBorderAnimation(symbol).getKeyFrame(stateTime, true)
            : Assets.I().getBase(symbol);
    }

    public void tickScale(float delta) {
        float speed = 15f; // higher = snappier
        scale += (targetScale - scale) * Math.min(1f, speed * delta);
    }

    public void updatePulse(boolean isSelected, float delta) {
        if (isSelected && !wasSelected) {
            pulseTime = 0f;
        }
        if (pulseTime < pulseDuration) {
            pulseTime += delta;
        }
        wasSelected = isSelected;
    }

    public float pulseScale() {
        if (pulseTime >= pulseDuration) return 1f;
        float a = pulseTime / pulseDuration;       // 0..1
        float bump = (float)Math.sin(Math.PI * a); // 0..1..0
        return 1f + bump * pulseAmp;               // peaks at 1 + amp
    }

    public float posX() { return posX; }
    public float posY() { return posY; }
}
