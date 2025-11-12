package com.avaricious.slot.pot;

public class Chip {
    public float scale = 1f;
    public float targetScale = 1f;

    private boolean wasHovered = false;
    private float wobbleTime = 0f;

    private final float wobbleDuration = 0.25f;  // seconds
    private final float wobbleAmpDeg   = 7f;     // peak rotation in degrees
    private final float wobbleScaleAmp = 0.03f;  // small elastic bump

    public void tickScale(float delta) {
        float speed = 15f; // match Slot snappiness
        scale += (targetScale - scale) * Math.min(1f, speed * delta);
    }

    public void updateHoverWobble(boolean isHovered, float delta) {
        if (isHovered && !wasHovered) wobbleTime = 0f;  // restart on hover entry
        if (wobbleTime < wobbleDuration) wobbleTime += delta;
        wasHovered = isHovered;
    }

    public float wobbleAngleDeg() {
        if (wobbleTime >= wobbleDuration) return 0f;
        float t = wobbleTime / wobbleDuration;                     // 0..1
        float decay = 1f - t;
        float oscill = (float)Math.sin((float)(Math.PI * 2.5 * t));// ring
        return wobbleAmpDeg * oscill * decay;
    }

    public float wobbleScale() {
        if (wobbleTime >= wobbleDuration) return 1f;
        float t = wobbleTime / wobbleDuration;
        float decay = 1f - t;
        float oscill = (float)Math.sin((float)(Math.PI * 2.5 * t));
        return 1f + Math.abs(oscill) * decay * wobbleScaleAmp;
    }
}

