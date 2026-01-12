package com.avaricious.upgrades.bars;

import com.avaricious.Assets;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.Slot;
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
    private final float UNFOLD_SPEED = 5f;

    private final float EPS = 0.001f;

    private final TextureRegion jokerTexture = new TextureRegion(Assets.I().getJokerCard());
    private final TextureRegion jokerShadowTexture = new TextureRegion(Assets.I().getJokerCardShadow());

    private final Map<Upgrade, Rectangle> jokerBounds = new LinkedHashMap<>();
    private final Map<Upgrade, Slot> jokerAnimationManagers = new LinkedHashMap<>();
    private final Rectangle deckBounds;

    // Animation state
    private float unfoldT = 0f;                 // 0 = folded, 1 = fully unfolded
    private final Map<Upgrade, Rectangle> foldedBounds = new LinkedHashMap<>();
    private final Map<Upgrade, Rectangle> unfoldedBounds = new LinkedHashMap<>();

    private Upgrade hoveringUpgrade;

    public JokerDeck(Rectangle deckBounds) {
        this.deckBounds = deckBounds;

        loadJokers();
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        hoveringUpgrade = null;
        boolean hoverDeck = deckBounds.contains(mouse);

        if (unfolded()) {
            Rectangle firstCardBounds = jokerBounds.values().iterator().next();
            for(Map.Entry<Upgrade, Rectangle> entry: jokerBounds.entrySet()) {
                firstCardBounds = entry.getValue();
                if(firstCardBounds.contains(mouse)) hoveringUpgrade = entry.getKey();
            }

            float width = (deckBounds.x + firstCardBounds.width) - firstCardBounds.x;
            float height = firstCardBounds.height;

            Rectangle hoverBounds = new Rectangle(
                firstCardBounds.x, firstCardBounds.y, width, height
            );
            if (hoverBounds.contains(mouse)) hoverDeck = true;
        }

        float target = hoverDeck ? 1f : 0f;
        unfoldT = expApproach(unfoldT, target, UNFOLD_SPEED, delta);

        // Recompute current animated bounds
        updateAnimatedBounds();
    }


    public void draw(SpriteBatch batch, float delta) {
        batch.setColor(1f, 1f, 1f, 0.25f);
        batch.draw(jokerShadowTexture,
            deckBounds.x - 0.15f, deckBounds.y - 0.15f,
            deckBounds.width + 0.3f, deckBounds.height + 0.3f);
        batch.setColor(1f, 1f, 1f, 1f);
        jokerBounds.forEach((upgrade, bounds) -> {
//            if(bounds.x < deckBounds.x - 0.1f) {
//                batch.setColor(1f, 1f, 1f, 0.25f);
//                batch.draw(jokerShadowTexture, bounds.x + 0.15f, bounds.y - 0.15f, bounds.width, bounds.height);
//                batch.setColor(1f, 1f, 1f, 1f);
//            }

            batch.draw(jokerTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        });

        if(hoveringUpgrade != null) PopupManager.I().showTooltip(hoveringUpgrade,
            getBoundsByUpgrade(hoveringUpgrade).x -1f, getBoundsByUpgrade(hoveringUpgrade).y + 2.75f);
    }

    private void updateAnimatedBounds() {
        float t = smoothstep(unfoldT); // <-- easing applied here

        for (Map.Entry<Upgrade, Rectangle> e : jokerBounds.entrySet()) {
            Upgrade up = e.getKey();
            Rectangle cur = e.getValue();
            Rectangle a = foldedBounds.get(up);
            Rectangle b = unfoldedBounds.get(up);

            cur.x = lerp(a.x, b.x, t);
            cur.y = lerp(a.y, b.y, t);
            cur.width  = lerp(a.width,  b.width,  t);
            cur.height = lerp(a.height, b.height, t);
        }
    }

    private void loadJokers() {
        jokerBounds.clear();
        foldedBounds.clear();
        unfoldedBounds.clear();

        List<Upgrade> upgrades = UpgradesManager.I().getDeck();

        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade up = upgrades.get(i);

            // Folded: stack on the deck (tiny offset for depth)
            // small stacked offset (optional)
            Rectangle folded = new Rectangle(
                deckBounds.x + i * FOLDED_STEP,
                deckBounds.y + i * FOLDED_STEP,
                deckBounds.width,
                deckBounds.height
            );

            // Unfolded: spread to the left; keep a slight Y offset if you like
            // how far each next card moves left (world units)
            Rectangle unfolded = unfoldedCardPos(i);


            foldedBounds.put(up, folded);
            unfoldedBounds.put(up, unfolded);

            // Start folded
            jokerBounds.put(up, new Rectangle(folded));
            jokerAnimationManagers.put(up, new Slot(new Vector2(folded.x, folded.y)));
        }
    }

    private Rectangle unfoldedCardPos(int i) {
        float baseX = deckBounds.x - (i * SPREAD_STEP_X);
        float baseY = deckBounds.y + i * FOLDED_STEP;

//        float LIFT_Y = 0.4f;
//        baseY += LIFT_Y;

        float unfoldedW = deckBounds.width;
        float unfoldedH = deckBounds.height;

        float centerX = baseX + deckBounds.width / 2f;
        float centerY = baseY + deckBounds.height / 2f;

        return new Rectangle(
            centerX - unfoldedW / 2f,
            centerY - unfoldedH / 2f,
            unfoldedW,
            unfoldedH
        );
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

    private boolean unfolded() {
        return unfoldT > EPS;
    }


}
