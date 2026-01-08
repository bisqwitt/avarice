package com.avaricious.effects;

public class PulseEffect {

    private final float PULSE_DURATION = 0.15f;

    private float pulseLifetime = 0f;

    private boolean wasActive;

    public void restart() {
        pulseLifetime = 0f;
    }

    public void tick(float delta) {
        if (pulseLifetime < PULSE_DURATION) pulseLifetime += delta;
    }

    public float getPulseValue() {
        if (pulseLifetime >= PULSE_DURATION) return 1f;

        float a = pulseLifetime / PULSE_DURATION;       // 0..1
        float bump = (float)Math.sin(Math.PI * a); // 0..1..0
        float PULSE_AMP = 0.175f;

        return 1f + bump * PULSE_AMP;               // peaks at 1 + amp
    }

}
