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

