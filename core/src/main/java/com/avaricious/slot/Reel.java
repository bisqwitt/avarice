package com.avaricious.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Reel {
    // Simple state machine: spin -> stop cleanly (no post-settle bounce)
    enum State { IDLE, ACCEL, CRUISE, DECEL }

    private final List<Symbol> strip;
    private final int rowsVisible;

    // Position in "symbol units" (continuous). Integer step == next symbol.
    private float pos;

    // Motion tuning (seconds)
    private float accelTime = 0.25f;
    private float cruiseTime = 0.80f;
    private float decelTime  = 0.90f;

    // Cruise target speed (symbols per second)
    private float baseSpeed;

    // Phase bookkeeping
    private State state = State.IDLE;
    private float tPhase = 0f;
    private float phaseDuration = 0f;

    // Stop targeting
    private boolean stopRequested = false;
    private float stopTarget;      // absolute pos we want to land on
    private float decelStartPos;   // pos when DECEL begins

    private final Random rng = new Random();

    public Reel(List<Symbol> strip, int rowsVisible) {
        if (strip == null || strip.isEmpty()) {
            throw new IllegalArgumentException("Reel strip must not be empty");
        }
        this.strip = new ArrayList<>(strip);
        this.rowsVisible = Math.max(1, rowsVisible);
        this.pos = rng.nextInt(this.strip.size()); // random starting offset
    }

    /** Begin spinning with an organic accel → cruise. */
    public void start(float speedSymbolsPerSec) {
        stopRequested = false;

        // Slight randomness so reels don't look identical
        baseSpeed = speedSymbolsPerSec * (0.95f + rng.nextFloat() * 0.10f);
        accelTime = 0.22f + rng.nextFloat() * 0.08f;
        cruiseTime = 0.70f + rng.nextFloat() * 0.25f;
        decelTime  = 0.85f + rng.nextFloat() * 0.20f;

        enter(State.ACCEL, accelTime);
    }

    /**
     * Request a stop aligned so the CENTER visible row lands on a symbol.
     * Handoff to DECEL is immediate (no cruise waiting), ensuring no dead zone.
     */
    public void stopSoonAlignCenter() {
        if (stopRequested) return;
        stopRequested = true;

        int size = strip.size();
        float base = (float) Math.floor(pos);
        int centerRow = rowsVisible / 2;
        int extraRot = 2 + rng.nextInt(3); // give room to decelerate visually

        stopTarget = base + extraRot * size + centerRow;

        // Ensure a minimum distance so decel always looks intentional
        float minDist = Math.max(3f, size * 0.75f);  // ≥ ~3 symbols or 75% of one rotation
        if (stopTarget - pos < minDist) stopTarget += size;

        beginDecelNow();
    }

    /** Advance the reel; call once per frame. */
    public void update(float delta) {
        if (state == State.IDLE) return;

        tPhase += delta;

        switch (state) {
            case ACCEL: {
                // Ease speed up to cruise
                float a = clamp01(tPhase / phaseDuration);
                float v = baseSpeed * easeOutCubic(a);
                pos += v * delta;

                // If a stop was requested, cut to DECEL after a brief visible ramp
                if (stopRequested && a > 0.6f) {
                    beginDecelNow();
                } else if (tPhase >= phaseDuration) {
                    enter(State.CRUISE, cruiseTime);
                }
            } break;

            case CRUISE: {
                pos += baseSpeed * delta;
                if (stopRequested || tPhase >= phaseDuration) {
                    beginDecelNow();
                }
            } break;

            case DECEL: {
                // Position interpolation ensures smooth motion from start to target
                float t = clamp01(tPhase / phaseDuration);
                float s = smoothstep(t); // monotonic 0..1
                pos = lerp(decelStartPos, stopTarget, s);

                if (t >= 1f) {
                    pos = Math.round(stopTarget); // snap perfectly to symbol index
                    enter(State.IDLE, 0f);
                }
            } break;
        }

        wrap();
    }

    /** Is the reel moving (any phase other than IDLE)? */
    public boolean isSpinning() { return state != State.IDLE; }

    /** Symbol visible at a given row index (0 = top row). */
    public Symbol symbolAtRow(int rowFromTop) {
        int baseIndex = (int) Math.floor(pos);
        int idx = (baseIndex + rowFromTop) % strip.size();
        if (idx < 0) idx += strip.size();
        return strip.get(idx);
    }

    /** Integer base index of the current top symbol. */
    public int baseIndex() { return (int) Math.floor(pos); }

    /** Fractional progress (0..1) toward the next symbol. */
    public float frac() {
        float f = pos - (float) Math.floor(pos);
        return (f < 0f) ? f + 1f : f;
    }

    // ---------- Internals ----------

    private void beginDecelNow() {
        decelStartPos = pos;
        enter(State.DECEL, decelTime);
    }

    private void enter(State st, float duration) {
        state = st;
        tPhase = 0f;
        phaseDuration = Math.max(0.0001f, duration);
    }

    private void wrap() {
        float size = strip.size();
        if (pos >= 1e7f || pos <= -1e7f) pos = pos % size;
        if (pos < 0) pos += size;
    }

    private static float clamp01(float x) { return x < 0 ? 0 : Math.min(x, 1); }
    private static float easeOutCubic(float t) { return 1f - (float) Math.pow(1f - t, 3); }
    private static float smoothstep(float t) { return t * t * (3f - 2f * t); }
    private static float lerp(float a, float b, float t) { return a + (b - a) * t; }
}
