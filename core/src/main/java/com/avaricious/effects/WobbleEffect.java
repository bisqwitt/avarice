package com.avaricious.effects;

public class WobbleEffect {

    private final float WOBBLE_ANGLE_DEGREE = 5f;       // peak rotation in degrees
    private final float WOBBLE_SCALE_AMP = 0.03f;

    private float wobbleTime = 0f;

    private boolean wasActive;

    public void tick(float delta) {
        float WOBBLE_DURATION = 0.25f;
        if (wobbleTime < WOBBLE_DURATION) wobbleTime += delta;
    }

    public void restart() {
        wobbleTime = 0f;
    }

    public float wobbleAngleDeg() {
        if (wobbleTime >= wobbleDuration) return 0f;
        float t = wobbleTime / wobbleDuration;              // 0..1
        float decay = 1f - t;                               // linear decay
        float oscill = (float)Math.sin((float)(Math.PI * 2.5 * t)); // ~1¼ swings
        return wobbleAmpDeg * oscill * decay;
    }

}
