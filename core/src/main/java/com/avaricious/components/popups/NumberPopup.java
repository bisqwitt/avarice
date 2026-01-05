package com.avaricious.components.popups;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class NumberPopup {
    private final List<TextureRegion> digitalNumberTextures = new ArrayList<>();
    private final TextureRegion plusTexture;
    private final TextureRegion percentageTexture;
    private final Vector2 position;
    private final Color color;

    // --- Timing (seconds) ---
    private final float pulseTime = 0.20f; // pop+wobble duration
    private final float holdTime  = 0.4f; // stay static
    private final float exitTime  = 0.25f; // shrink until gone
    private final float lifetime  = pulseTime + holdTime + exitTime;

    private float timeAlive = 0f;

    private final boolean asPercentage;

    private Runnable onFinished;

    public NumberPopup(int number, Color color, float x, float y, boolean asPercentage) {
        setDigitalNumberTextures(number);

        this.plusTexture = new TextureRegion(Assets.I().getPlusSymbol());
        this.percentageTexture = new TextureRegion(Assets.I().getPercentageSymbol());

        this.color = color;
        this.position = new Vector2(x, y);
        this.asPercentage = asPercentage;
    }

    public void transform(int newValue) {
        setDigitalNumberTextures(newValue);
        timeAlive = 0f;
    }

    public boolean isFinished() {
        return timeAlive >= lifetime;
    }

    public void update(float delta) {
        timeAlive += delta;
        if (timeAlive > lifetime) {
            timeAlive = lifetime;
            if(onFinished != null) onFinished.run();
        }
    }

    public void render(SpriteBatch batch) {
        float scale = getScale();
        float rotation = getRotation();
        float alpha = getAlpha();

        // If you want a hard cutoff instead of drawing tiny values:
        if (alpha <= 0f || scale <= 0f) return;

        float width = 7 / 20f;
        float height = 11 / 20f;
        float originX = width / 2f;
        float originY = height / 2f;

        // Use alpha here (previously you always used 1f)
        batch.setColor(color.r, color.g, color.b, alpha);

        batch.draw(
            plusTexture,
            position.x - originX - 0.5f, position.y - originY,
            originX, originY,
            width, height,
            scale, scale,
            rotation
        );

        for (int i = 0; i < digitalNumberTextures.size(); i++) {
            batch.draw(
                digitalNumberTextures.get(i),
                position.x - originX + (0.5f * i), position.y - originY,
                originX, originY,
                width, height,
                scale, scale,
                rotation
            );
        }

        if(asPercentage) {
            batch.draw(
                percentageTexture,
                position.x - originX + (0.5f * digitalNumberTextures.size() -1), position.y - originY,
                originX, originY,
                8 / 20f, 13 / 20f,
                scale, scale,
                rotation
            );
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }

    private float getPulseCurve() {
        // 0..1 within pulse phase only
        float t = clamp01(timeAlive / pulseTime);

        // Parabola: 0 -> 1 -> 0
        float pulse = 1f - 4f * (t - 0.5f) * (t - 0.5f);
        return Math.max(0f, pulse);
    }

    private float getScale() {
        float baseScale = 1.0f;

        // Phase 1: pulse
        if (timeAlive <= pulseTime) {
            float pulseCurve = getPulseCurve();
            float pulseScale = 0.35f;
            return baseScale + pulseCurve * pulseScale;
        }

        // Phase 2: hold (static)
        if (timeAlive <= pulseTime + holdTime) {
            return baseScale;
        }

        // Phase 3: exit (shrink)
        float t = (timeAlive - (pulseTime + holdTime)) / exitTime; // 0..1
        t = clamp01(t);

        // Ease-in shrink looks more natural than linear
        float k = easeInQuad(t);
        return baseScale * (1f - k); // 1 -> 0
    }

    private float getRotation() {
        // wobble only during pulse, then static
        if (timeAlive <= pulseTime) {
            float wobbleAngle = 8f;
            return getPulseCurve() * wobbleAngle;
        }
        return 0f;
    }

    private float getAlpha() {
        // fully visible during pulse + hold
        if (timeAlive <= pulseTime + holdTime) {
            return 1f;
        }

        // optional fade during exit (recommended)
        float t = (timeAlive - (pulseTime + holdTime)) / exitTime; // 0..1
        t = clamp01(t);
        return 1f - t; // 1 -> 0
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
        String.valueOf(number)
            .chars()
            .map(Character::getNumericValue)
            .forEach(digit -> digitalNumberTextures.add(new TextureRegion(Assets.I().getDigitalNumber(digit))));
    }
}
