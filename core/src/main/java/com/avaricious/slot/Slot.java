package com.avaricious.slot;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;

public class Slot {

    private SlotListener listener;
    private Symbol type;
    private float stateTime = 0f;
    private final float posX;
    private final float posY;
    private boolean spinning = false;

    public float scale = 1f;
    public float targetScale = 1f;

    private boolean wasSelected = false;
    private float pulseTime = 0f;
    private final float pulseDuration = 0.15f;
    private final float pulseAmp = 0.125f;

    public Slot(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;

        changeSymbol();
    }

    public void setListener(SlotListener listener) {
        this.listener = listener;
    }

    public void spin(float time) {
        spinning = true;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                spinning = false;
                listener.onSpinFinished(Slot.this);
            }
        }, time);
    }

    public TextureRegion getFrame(boolean selected, float delta) {
        if(selected) stateTime += delta;
        return selected ? Assets.I().getBorderAnimation(type).getKeyFrame(stateTime, true)
            : Assets.I().getBase(type);
    }

    public void tickScale(float delta) {
        float speed = 15f; // higher = snappier
        scale += (targetScale - scale) * Math.min(1f, speed * delta);
    }

    public void updatePulse(boolean isSelected, float delta) {
        // trigger once when selection turns true
        if (isSelected && !wasSelected) {
            pulseTime = 0f; // restart pulse
        }
        // advance pulse while active
        if (pulseTime < pulseDuration) {
            pulseTime += delta;
        }
        wasSelected = isSelected;
    }

    // multiplicative pulse scale (1.0 when inactive)
    public float pulseScale() {
        if (pulseTime >= pulseDuration) return 1f;
        float a = pulseTime / pulseDuration;       // 0..1
        float bump = (float)Math.sin(Math.PI * a); // 0..1..0
        return 1f + bump * pulseAmp;               // peaks at 1 + amp
    }

    public void changeSymbol() {
        type = SymbolManager.I().randomSymbolWithSpawnChance();
    }

    public boolean isSpinning() {
        return spinning;
    }

    public Symbol type() {
        return type;
    }

    public float posX() {
        return posX;
    }

    public float posY() {
        return posY;
    }

}

