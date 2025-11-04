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

    // --- NEW: hover wobble state ---
    private boolean wasHovered = false;
    private float wobbleTime = 0f;
    private final float wobbleDuration = 0.25f;  // seconds
    private final float wobbleAmpDeg = 7f;       // peak rotation in degrees
    private final float wobbleScaleAmp = 0.03f;  // tiny elastic bump

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
        // selection entry wobble
        if (isSelected && !wasSelected) {
            pulseTime = 0f;
            wobbleTime = 0f; // wobble on selection entry
        }

        // DESELECTION wobble
        if (!isSelected && wasSelected) {
            wobbleTime = 0f; // NEW: wobble when deselecting
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

    public void updateHoverWobble(boolean isHovered, float delta) {
        if (isHovered && !wasHovered) {
            wobbleTime = 0f; // restart wobble on hover entry, regardless of selection
        }
        if (wobbleTime < wobbleDuration) {
            wobbleTime += delta;
        }
        wasHovered = isHovered;
    }

    /** Current wobble rotation in degrees (damped sine). */
    public float wobbleAngleDeg() {
        if (wobbleTime >= wobbleDuration) return 0f;
        float t = wobbleTime / wobbleDuration;              // 0..1
        float decay = 1f - t;                               // linear decay
        float oscill = (float)Math.sin((float)(Math.PI * 2.5 * t)); // ~1¼ swings
        return wobbleAmpDeg * oscill * decay;
    }

    /** Small elastic scale multiplier for wobble (optional, subtle). */
    public float wobbleScale() {
        if (wobbleTime >= wobbleDuration) return 1f;
        float t = wobbleTime / wobbleDuration;
        float decay = 1f - t;
        float oscill = (float)Math.sin((float)(Math.PI * 2.5 * t));
        return 1f + Math.abs(oscill) * decay * wobbleScaleAmp;
    }

    public float posX() { return posX; }
    public float posY() { return posY; }
}
