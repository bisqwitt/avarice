package com.avaricious.components.popups;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class NumberPopup {

    private final TextureRegion texture;
    private final TextureRegion plusTexture;
    private final TextureRegion whiteTexture;
    private final Vector2 position;
    private final Color color;

    private final float lifetime = 1f;      // total duration in seconds
    private float timeAlive = 0f;

    public NumberPopup(Texture texture, Color color, float x, float y) {
        this.texture = new TextureRegion(texture);
        this.plusTexture = new TextureRegion(Assets.I().getPlusSymbol());
        this.whiteTexture = new TextureRegion(Assets.I().getWhiteTexture());

        this.color = color;
        this.position = new Vector2(x, y);
    }

    public boolean isFinished() {
        return timeAlive >= lifetime;
    }

    public void update(float delta) {
        timeAlive += delta;
        if (timeAlive > lifetime) {
            timeAlive = lifetime;
        }
    }

    public void render(SpriteBatch batch) {
        float progress = timeAlive / lifetime;  // 0..1

        // Fade out over full lifetime
        float initialAlpha = 1f;
        float alpha = initialAlpha * (1f - progress / 2);

        // ---- SINGLE FAST PULSE + WOBBLE ----
        float pulseDuration = 0.2f; // fraction of lifetime used for the pulse
        float t = progress / pulseDuration;
        if (t > 1f) t = 1f;         // clamp after pulse, keep at end value

        // Parabola: 0 -> 1 -> 0 once
        float pulseCurve = 1f - 4f * (t - 0.5f) * (t - 0.5f);
        if (pulseCurve < 0f) pulseCurve = 0f; // numerical safety

        // Scale
        float baseScale = 1.0f;
        float pulseScale = 0.35f;   // intensity of the pop, tweak to taste
        float scale = baseScale + pulseCurve * pulseScale;

        // Wobble
        float wobbleAngle = 8f;     // degrees, tweak to taste
        float rotation = pulseCurve * wobbleAngle;

        // Draw centered
        float width = 7 / 20f;
        float height = 11 / 20f;
        float originX = width / 2f;
        float originY = height / 2f;

        batch.setColor(1f, 1f, 1f, 1f);
//        batch.draw(
//            whiteTexture,
//            position.x - originX - 0.125f, position.y - originY - 0.1f,
//            originX, originY,
//            0.65f, 0.65f,
//            scale, scale,
//            rotation + 45
//        );

        batch.setColor(color.r, color.g, color.b, 1f);
        batch.draw(
            plusTexture,
            position.x - originX - 0.5f, position.y - originY,
            originX, originY,
            width, height,
            scale, scale,
            rotation
        );
        batch.draw(
            texture,
            position.x - originX, position.y - originY,
            originX, originY,
            width, height,
            scale, scale,
            rotation
        );
        batch.setColor(1f, 1f, 1f, 1f);
    }
}
