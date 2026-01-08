package com.avaricious.components.slot;

import com.avaricious.Assets;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.effects.PulseEffect;
import com.avaricious.effects.WobbleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class ObjectWithPopEffect {

    private final PulseEffect pulseEffect = new PulseEffect();
    private final WobbleEffect wobbleEffect = new WobbleEffect();
    private final boolean tooltipOnTopOfCard;

    private float stateTime = 0f;
    public float scale = 1f;
    public float targetScale = 1f;

    // tiny elastic bump
    private Rectangle bounds;

    public ObjectWithPopEffect(Rectangle spawnBounds) {
        bounds = spawnBounds;
    }

    public TextureRegion getFrame(Symbol symbol, boolean selected, float delta) {
        if (selected) stateTime += delta;
        return selected
            ? Assets.I().getBorderAnimation(symbol).getKeyFrame(stateTime, true)
            : Assets.I().getBase(symbol);
    }

    public void handleInput(float delta) {
        pulseEffect.tick(delta);
        wobbleEffect.tick(delta);
        float speed = 15f; // higher = snappier
        scale += (targetScale - scale) * Math.min(1f, speed * delta);
    }

    public void wobble() {
        wobbleEffect.restart();
    }

    public void pulse() {
        pulseEffect.restart();
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

    public Rectangle getBounds() {
        return bounds;
    }

    public void draw(SpriteBatch batch, float delta, boolean  isInGrid) {
        s = objectWithPopEffect.scale * objectWithPopEffect.pulseScale() * objectWithPopEffect.wobbleScale();

        float drawW = bounds.width * s;
        float drawH = bounds.height * s;
        float adjX = drawX - (drawW - CELL_WIDTH) / 2f;
        float adjY = drawY - (drawH - CELL_HEIGHT) / 2f;

        // choose frame (keeps your animated border when selected)
        region = isInGrid
            ? grid[c][k].getFrame(symbolSlot.symbol(), selected, delta)
            : Assets.I().getBase(symbolSlot.symbol());

        // NEW: rotate around center using current wobble angle
        float rotation = isInGrid ? grid[c][k].wobbleAngleDeg() : 0f;

        float boxW = drawW + 0.2f;
        float boxH = drawH + 0.2f;

        // Slot Box Shadow
        batch.setColor(1f, 1f, 1f, 0.25f);
        batch.draw(
            slotBoxShadow,
            adjX - 0.1f + 0.05f, adjY - 0.1f - 0.05f,
            boxW / 2f, boxH / 2f,
            boxW, boxH,
            1f, 1f,
            rotation
        );

        // Slot Box
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(
            slotBox,
            adjX - 0.1f, adjY - 0.1f,
            boxW / 2f, boxH / 2f,
            boxW, boxH,
            1f, 1f,
            rotation
        );

        // Symbol Shadow
        batch.setColor(1f, 1f, 1f, 0.25f);
        batch.draw(
            Assets.I().getSymbolShadow(symbolSlot.symbol()),
            adjX + 0.05f, adjY - 0.05f,
            drawW / 2f, drawH / 2f,
            drawW, drawH,
            1f, 1f,
            rotation
        );

        // Symbol
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(
            region,
            adjX, adjY,
            drawW / 2f, drawH / 2f,   // originX, originY
            drawW, drawH,
            1f, 1f,                   // scale already baked into drawW/H
            rotation
        );

        if(symbolSlot.statUpgrade() != null) {
            batch.draw(
                new TextureRegion(symbolSlot.statUpgrade().getStat().getTexture()),
                adjX + 1.45f, adjY + 0.6f,
                (drawW - 2f) / 2f, (drawW - 2f) / 2f,
                (drawW - 1.8f), (drawW - 1.8f),
                1f, 1f,
                rotation + 180
            );
        }
    }
}
