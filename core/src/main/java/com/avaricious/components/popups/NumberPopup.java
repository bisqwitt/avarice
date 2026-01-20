package com.avaricious.components.popups;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class NumberPopup {

    private enum Phase { PULSE, HOLD, EXIT, FINISHED }

    private final List<TextureRegion> digitalNumberTextures = new ArrayList<>();
    private final List<TextureRegion> digitalNumberShadowTextures = new ArrayList<>();

    private final TextureRegion plusTexture = new TextureRegion(Assets.I().getPlusSymbol());
    private final TextureRegion percentageTexture = new TextureRegion(Assets.I().getPercentageSymbol());

    private final Rectangle bounds;
    private final Color color;

    private final float pulseTime = 0.20f; // pop+wobble duration
    private final float holdTime  = 0.40f; // OLD behavior: stay static for this long
    private final float exitTime  = 0.25f; // shrink until gone

    private final boolean manualHold;

    private Phase phase = Phase.PULSE;

    // One timer reused for the current phase (0..phaseDuration)
    private float timeInPhase = 0f;

    private final boolean asPercentage;

    private Runnable onFinished;

    public NumberPopup(int number, Color color, float x, float y, boolean asPercentage, boolean manualHold) {
        this.color = color;
        this.asPercentage = asPercentage;
        this.manualHold = manualHold;

        this.bounds = new Rectangle(x, y, 7 / 15f, 11 / 15f);
        setDigitalNumberTextures(number);
        restart();
    }

    public void release() {
        if (manualHold && phase == Phase.HOLD) {
            phase = Phase.EXIT;
            timeInPhase = 0f;
        }
    }

    public boolean isManualHold() {
        return manualHold;
    }

    public void transform(int newValue) {
        setDigitalNumberTextures(newValue);
        restart();
    }

    private void restart() {
        phase = Phase.PULSE;
        timeInPhase = 0f;
    }

    public boolean isFinished() {
        return phase == Phase.FINISHED;
    }

    public boolean isHolding() {
        return phase == Phase.HOLD;
    }

    public void update(float delta) {
        if (phase == Phase.FINISHED) return;

        timeInPhase += delta;

        switch (phase) {
            case PULSE -> {
                if (timeInPhase >= pulseTime) {
                    phase = Phase.HOLD;
                    timeInPhase = 0f;
                }
            }
            case HOLD -> {
                // Old behavior: auto-exit after holdTime
                if (!manualHold && timeInPhase >= holdTime) {
                    phase = Phase.EXIT;
                    timeInPhase = 0f;
                }
                // New behavior: do nothing until release()
            }
            case EXIT -> {
                if (timeInPhase >= exitTime) {
                    timeInPhase = exitTime;
                    phase = Phase.FINISHED;
                    if (onFinished != null) onFinished.run();
                }
            }
            case FINISHED -> {
                // nothing
            }
        }
    }

    public void render(SpriteBatch batch, float delta) {
        if (phase == Phase.FINISHED) return;

        float scale = getScale();
        float rotation = getRotation();
        float alpha = getAlpha();

        if (alpha <= 0f || scale <= 0f) return;

        float originX = bounds.width / 2f;
        float originY = bounds.height / 2f;

        // Use alpha for main draw color
        batch.setColor(color.r, color.g, color.b, alpha);

        batch.draw(
            plusTexture,
            bounds.x - 0.5f, bounds.y,
            originX, originY,
            bounds.width, bounds.height,
            scale, scale,
            rotation
        );

        for (int i = 0; i < digitalNumberTextures.size(); i++) {
            // Shadow
            batch.setColor(1f, 1f, 1f, 0.25f);
            batch.draw(
                digitalNumberShadowTextures.get(i),
                bounds.x + (0.5f * i) + 0.1f, bounds.y - 0.1f,
                originX, originY,
                bounds.width, bounds.height,
                scale, scale,
                rotation
            );

            // Foreground digit
            batch.setColor(color.r, color.g, color.b, alpha);
            batch.draw(
                digitalNumberTextures.get(i),
                bounds.x + (0.5f * i), bounds.y,
                originX, originY,
                bounds.width, bounds.height,
                scale, scale,
                rotation
            );
        }

        if (asPercentage) {
            batch.draw(
                percentageTexture,
                bounds.x + 0.4f, bounds.y,
                originX, originY,
                8 / 20f, 13 / 20f,
                scale, scale,
                rotation
            );
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }

    private float getPulseCurve() {
        float t = clamp01(timeInPhase / pulseTime);
        float pulse = 1f - 4f * (t - 0.5f) * (t - 0.5f);
        return Math.max(0f, pulse);
    }

    private float getScale() {
        float baseScale = 1.0f;

        if (phase == Phase.PULSE) {
            float pulseScale = 0.35f;
            return baseScale + getPulseCurve() * pulseScale;
        }

        if (phase == Phase.HOLD) {
            return baseScale;
        }

        // EXIT
        float t = clamp01(timeInPhase / exitTime);
        return baseScale * (1f - easeInQuad(t));
    }

    private float getRotation() {
        if (phase == Phase.PULSE) {
            float wobbleAngle = 8f;
            return getPulseCurve() * wobbleAngle;
        }
        return 0f;
    }

    private float getAlpha() {
        if (phase == Phase.PULSE || phase == Phase.HOLD) {
            return 1f;
        }

        // EXIT fade
        float t = clamp01(timeInPhase / exitTime);
        return 1f - t;
    }

    private float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private float easeInQuad(float t) {
        return t * t;
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    private void setDigitalNumberTextures(int number) {
        digitalNumberTextures.clear();
        digitalNumberShadowTextures.clear(); // IMPORTANT: keep lists in sync

        String.valueOf(number)
            .chars()
            .map(Character::getNumericValue)
            .forEach(digit -> {
                digitalNumberTextures.add(new TextureRegion(Assets.I().getDigitalNumber(digit)));
                digitalNumberShadowTextures.add(new TextureRegion(Assets.I().getDigitalNumberShadow(digit)));
            });
    }
}
