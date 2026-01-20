package com.avaricious.upgrades.bars;

import com.avaricious.Assets;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.Slot;
import com.avaricious.screens.ScreenManager;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JokerDeck {

    private final float FOLDED_STEP = 0.02f;
    private final float SPREAD_STEP_X = 2.25f;
    private final float UNFOLDED_SCALE = 1.15f;
    private final float UNFOLD_SPEED = 2f;

    private final float CARD_RAMP  = 0.5f;     // how much of the remaining time each card uses to reach 1
    private final float CARD_EASE_POWER = 0.9f; // >1 = snappier finish; <1 = softer

    private final float PICK_PHASE = 0.35f; // fraction of each card's timeline reserved for "pick up" (scale)

    private final float EPS = 0.001f;

    private final TextureRegion jokerTexture = new TextureRegion(Assets.I().getJokerCard());
    private final TextureRegion jokerShadowTexture = new TextureRegion(Assets.I().getJokerCardShadow());

    private final Map<Upgrade, Rectangle> jokerBounds = new LinkedHashMap<>();
    private final Map<Upgrade, Slot> jokerAnimationManagers = new LinkedHashMap<>();
    private final Rectangle deckBounds;

    private float unfoldT = 0f;                 // 0 = folded, 1 = fully unfolded
    private final Map<Upgrade, Rectangle> foldedBounds = new LinkedHashMap<>();
    private final Map<Upgrade, Rectangle> unfoldedBounds = new LinkedHashMap<>();
    private final Map<Upgrade, Float> pickProgress = new LinkedHashMap<>();

    private Upgrade hoveringUpgrade;

    public JokerDeck(Rectangle deckBounds) {
        this.deckBounds = deckBounds;

        loadJokers(UpgradesManager.I().getDeck());

        UpgradesManager.I().onDeckChange(this::loadJokers);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        if(jokerBounds.isEmpty()) return;

        Upgrade hoveringUpgradeLastRender = hoveringUpgrade;
        hoveringUpgrade = null;
        boolean hoveringDeck = deckBounds.contains(mouse);

        if (unfolded()) {
            jokerBounds.forEach((upgrade, bounds) -> {
                if(bounds.contains(mouse)) {
                    hoveringUpgrade = upgrade;
                }
            });

            if (getUnfoldedAllCardBounds().contains(mouse)) hoveringDeck = true;
        }

        float target = hoveringDeck ? 1f : 0f;
        unfoldT = expApproach(unfoldT, target, UNFOLD_SPEED, delta);

        if(hoveringUpgradeLastRender == null && hoveringUpgrade != null) {
            Slot slot = jokerAnimationManagers.get(hoveringUpgrade);
//            slot.pulse();
//            slot.wobble();
        }

        // Recompute current animated bounds
        updateAnimatedBounds();
        jokerAnimationManagers.forEach((upgrade, slot) -> {
            slot.updateHoverWobble(true, delta);
            slot.updatePulse(false, delta);
        });
    }


    public void draw(SpriteBatch batch, float delta) {
        batch.setColor(1f, 1f, 1f, 0.25f);
        batch.draw(jokerShadowTexture,
            deckBounds.x - 0.15f, deckBounds.y - 0.15f,
            deckBounds.width + 0.3f, deckBounds.height + 0.3f);
        batch.setColor(1f, 1f, 1f, 1f);
        jokerBounds.forEach((upgrade, bounds) -> {

            float pickProgress = clamp01(this.pickProgress.get(upgrade));
            float shadowAlpha = 0.25f * pickProgress;
            float shadowXOffset = 0.15f * pickProgress;
            float shadowYOffset = -0.15f * pickProgress;

            batch.setColor(1f, 1f, 1f, shadowAlpha);
            batch.draw(jokerShadowTexture, bounds.x + shadowXOffset, bounds.y + shadowYOffset, bounds.width, bounds.height);
            batch.setColor(1f, 1f, 1f, 1f);

            float originX = bounds.width * 0.5f;
            float originY = bounds.height * 0.5f;

            Slot slot = jokerAnimationManagers.get(upgrade);
            float s = slot.pulseScale() * slot.wobbleScale(); // combined scale multipliers
            float r = slot.wobbleAngleDeg();                  // degrees

            batch.draw(
                jokerTexture,
                bounds.x, bounds.y,
                originX, originY,
                bounds.width, bounds.height,
                s, s,
                r
            );
        });

        if(hoveringUpgrade != null) PopupManager.I().showTooltip(hoveringUpgrade,
            getBoundsByUpgrade(hoveringUpgrade).x -1f, getBoundsByUpgrade(hoveringUpgrade).y + 2.5f);
    }

    private void updateAnimatedBounds() {
        float globalUnfold = smoothstep(unfoldT);     // driven by hover

        int i = 0;
        int n = jokerBounds.size();

        for (Map.Entry<Upgrade, Rectangle> e : jokerBounds.entrySet()) {
            Upgrade up = e.getKey();
            Rectangle cur = e.getValue();
            Rectangle a = foldedBounds.get(up);
            Rectangle b = unfoldedBounds.get(up);

            // normal per-card unfold timeline
            float tUnfold = cardTimeline(globalUnfold, (n - 1) - i, n);
            tUnfold = easeOutPow(tUnfold, CARD_EASE_POWER);

            // combine: highlighted card can move even when unfolded is 0
            float t = tUnfold;

            float tScale = clamp01(t / PICK_PHASE);
            float tMove  = clamp01((t - PICK_PHASE) / (1f - PICK_PHASE));

            tScale = easeOutPow(tScale, 1.8f);
            tMove  = smoothstep(tMove);

            cur.width  = lerp(a.width,  b.width,  tScale);
            cur.height = lerp(a.height, b.height, tScale);

            cur.x = lerp(a.x, b.x, tMove);

            pickProgress.put(up, tScale);
            i++;
        }
    }


    private void loadJokers(List<? extends Upgrade> upgrades) {
        jokerBounds.clear();
        foldedBounds.clear();
        unfoldedBounds.clear();
        pickProgress.clear();

        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade up = upgrades.get(i);

            // Folded: stack on the deck (tiny offset for depth)
            // small stacked offset (optional)
            Rectangle folded = new Rectangle(
                deckBounds.x,
                deckBounds.y + i * FOLDED_STEP,
                deckBounds.width,
                deckBounds.height
            );

            Rectangle unfolded = unfoldedCardPos(i);

            foldedBounds.put(up, folded);
            unfoldedBounds.put(up, unfolded);

            jokerBounds.put(up, new Rectangle(folded));
            jokerAnimationManagers.put(up, new Slot(new Vector2(folded.x, folded.y)));

            pickProgress.put(up, 0f);
        }
    }

    private Rectangle unfoldedCardPos(int i) {
        float baseX = deckBounds.x + (i * SPREAD_STEP_X);
        float baseY = deckBounds.y;

        float unfoldedW = deckBounds.width * UNFOLDED_SCALE;
        float unfoldedH = deckBounds.height * UNFOLDED_SCALE;

        float centerX = baseX + deckBounds.width / 2f;
        float centerY = baseY + deckBounds.height / 2f;

        return new Rectangle(
            centerX - unfoldedW / 2f,
            centerY - unfoldedH / 2f,
            unfoldedW,
            unfoldedH
        );
    }

    private Rectangle getUnfoldedAllCardBounds() {
        Rectangle firstCardBounds = null;
        for(Map.Entry<Upgrade, Rectangle> entry: unfoldedBounds.entrySet()) {
            firstCardBounds = entry.getValue();
        }

        float width = deckBounds.x + firstCardBounds.x + firstCardBounds.width;
        float height = firstCardBounds.height;

        return new Rectangle(deckBounds.x, deckBounds.y, width, height);
    }

    public Rectangle getBoundsByUpgrade(Upgrade upgrade) {
        return jokerBounds.get(upgrade);
    }

    public Slot getSlotByUpgrade(Upgrade upgrade) {
        return jokerAnimationManagers.get(upgrade);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float expApproach(float value, float target, float speed, float delta) {
        // speed is in 1/seconds (larger = snappier)
        float alpha = 1f - (float)Math.exp(-speed * delta);
        return value + (target - value) * alpha;
    }


    private static float smoothstep(float t) {
        // clamp
        if (t < 0f) return 0f;
        if (t > 1f) return 1f;
        // smoothstep: 3t^2 - 2t^3
        return t * t * (3f - 2f * t);
    }

    private float cardTimeline(float globalT, int index, int n) {
        // Fit all cards into [0..1] regardless of n
        float usable = Math.max(0.0001f, 1f - CARD_RAMP); // space left for staggering
        float delay = (n <= 1) ? 0f : (usable / (n - 1));

        float start = index * delay;
        float end = start + CARD_RAMP;
        return clamp01((globalT - start) / (end - start));
    }


    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }

    // Ease out power curve: fast start, crisp settle
    private static float easeOutPow(float t, float power) {
        t = clamp01(t);
        return 1f - (float)Math.pow(1f - t, power);
    }

    private boolean unfolded() {
        return unfoldT > EPS;
    }


}
