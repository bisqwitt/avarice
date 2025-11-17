package com.avaricious.components.slot;

import java.util.ArrayList;
import java.util.Collections;
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

    private int lockedBaseIndex = -1;
    private boolean hasLockedIndex = false;

    // Late-decel visual: use final base index early and animate only frac
    private boolean forceFracActive = false;
    private float forcedFrac = 0f;
    private float forcedFracInit = 0f;
    private static final float TCROSS = 0.85f; // start "final settle" phase

    private static final float EPS = 1e-4f;

    private Runnable onSpinFinished;

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
        hasLockedIndex = false;
        forceFracActive = false;   // <-- important
        forcedFrac = 0f;

        Collections.shuffle(strip);

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

        // More runway for a softer stop
        int extraRot = 3 + rng.nextInt(3); // 3..5 full rotations
        stopTarget = base + extraRot * size + centerRow;

        // Ensure a decent distance (≥ ~1.25 rotations)
        float minDist = Math.max(4f, size * 1.25f);
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
                float t = clamp01(tPhase / phaseDuration);

                // Overshoot 1/4 symbol *after* the final stop (downward), then settle back up
                float preTarget = stopTarget + 0.25f;

                if (t < TCROSS) {
                    // Phase 1: move towards preTarget with easing
                    float s = easeOutQuint(t / TCROSS);
                    pos = lerp(decelStartPos, preTarget, s);
                } else {
                    // Phase 2: lock to the FINAL base and animate frac DOWN 0.25 -> ~0.0 (settling upward)
                    int size = strip.size();
                    int finalBase = ((int) Math.floor(stopTarget)) % size;
                    if (finalBase < 0) finalBase += size;

                    if (!hasLockedIndex) {
                        lockedBaseIndex = finalBase;     // lock to final base, not previous
                        hasLockedIndex = true;

                        forceFracActive = true;
                        forcedFracInit = 0.25f;          // we overshot by +0.25
                        forcedFrac = forcedFracInit;     // start from 0.25
                    }

                    float s = easeOutQuint((t - TCROSS) / (1f - TCROSS));
                    // Animate frac downward: 0.25 -> ~0.0
                    forcedFrac = forcedFracInit - (forcedFracInit - EPS) * s;

                    // Move position back from preTarget to stopTarget
                    pos = lerp(preTarget, stopTarget, s);
                }

                if (t >= 1f) {
                    pos = stopTarget; // exact land

                    int size = strip.size();
                    int finalBase = ((int) Math.floor(stopTarget)) % size;
                    if (finalBase < 0) finalBase += size;

                    lockedBaseIndex = finalBase; // stay locked on the final base while idle
                    hasLockedIndex = true;

                    forceFracActive = true;
                    forcedFrac = EPS; // ~0.0 so visual == final index at frac==0

                    enter(State.IDLE, 0f);
                    if (onSpinFinished != null) onSpinFinished.run();
                }
            } break;

        }

        wrap();
    }

    /** Is the reel moving (any phase other than IDLE)? */
    public boolean isSpinning() { return state != State.IDLE; }

    /** Symbol visible at a given row index (0 = top row). */
    public Symbol symbolAtRow(int rowFromTop) {
        int size = strip.size();
        int baseIndex = hasLockedIndex
            ? lockedBaseIndex
            : (int)Math.floor(pos);
        if (baseIndex < 0) baseIndex += size;

        int idx = (baseIndex + rowFromTop) % size;
        if (idx < 0) idx += size;
        return strip.get(idx);
    }

    /** Integer base index of the current top symbol. */
    public int baseIndex() { return (int) Math.floor(pos); }

    /** Fractional progress (0..1) toward the next symbol. */
    public float frac() {
        if (forceFracActive) return forcedFrac;        // stays 1.0 when idle
        if (state == State.IDLE && hasLockedIndex) return 0f;
        float f = pos - (float)Math.floor(pos);
        return (f < 0f) ? f + 1f : f;
    }

    // ---------- Internals ----------

    private void beginDecelNow() {
        decelStartPos = pos;

        float distance = stopTarget - decelStartPos; // forward distance
        // Map distance to time: ~0.9s at ~1.25R → up to ~1.8s at ~3R
        float size = strip.size();
        float dRots = distance / size;
        float tMin = 0.90f, tMax = 1.80f;
        float rMin = 1.25f, rMax = 3.0f;
        float u = clamp01((dRots - rMin) / (rMax - rMin));
        decelTime = tMin + (tMax - tMin) * u;

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

    public void setOnSpinFinished(Runnable onSpinFinished) {
        this.onSpinFinished = onSpinFinished;
    }

    private static float clamp01(float x) { return x < 0 ? 0 : Math.min(x, 1); }
    private static float easeOutCubic(float t) { return 1f - (float) Math.pow(1f - t, 3); }
    private static float easeOutQuint(float t) { return 1f - (float)Math.pow(1f - t, 5); }
    private static float smoothstep(float t) { return t * t * (3f - 2f * t); }
    private static float lerp(float a, float b, float t) { return a + (b - a) * t; }
}
