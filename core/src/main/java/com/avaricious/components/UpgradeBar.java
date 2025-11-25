package com.avaricious.components;

import com.avaricious.Assets;
import com.avaricious.components.slot.Slot;   // <-- import your Slot
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeBar {

    private final TextureRegion jokerCard;
    private final TextureRegion cardShadow;

    private final Map<Upgrade, Rectangle> cardBounds = new HashMap<>();
    private final Map<Upgrade, Slot> cardAnimationManagers = new HashMap<>();

    private Upgrade hoveringKey = null;

    public UpgradeBar() {
        jokerCard = new TextureRegion(Assets.I().getJokerCard());
        cardShadow = new TextureRegion(Assets.I().getJokerCardShadow());

        // original values
        // card layout in world units
        float x = 3.5f;
        float y = 0.5f;
        float baseWidth = 142 / 115f;
        float baseHeight = 190 / 115f;

        List<Upgrade> upgrades = UpgradesManager.I().getUpgrades();
        for(int i = 0; i < upgrades.size(); i++) {
            Upgrade upgrade = upgrades.get(i);
            cardBounds.put(upgrade, new Rectangle(x + (i * 1.75f), y, baseWidth, baseHeight));
            cardAnimationManagers.put(upgrade, new Slot(new Vector2(x, y)));
        }
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        hoveringKey = null;
        cardBounds.forEach(((upgrade, rectangle) -> {
            boolean hovered = rectangle.contains(mouse);
            boolean selected = ((pressed && !wasPressed) || (!pressed && wasPressed)) && rectangle.contains(mouse);

            boolean highlighted = hovered || selected;

            Slot cardSlot = cardAnimationManagers.get(upgrade);
            // same pattern as SlotMachine
            cardSlot.targetScale = highlighted ? 1.125f : 1f;

            cardSlot.updatePulse(selected, delta);
            cardSlot.updateHoverWobble(hovered, delta);
            cardSlot.tickScale(delta);

            if(hovered) hoveringKey = upgrade;
        }));
    }

    /**
     * Draw the card using Slot's wobble/scale.
     */
    public void draw(SpriteBatch batch) {
        // combined scale (identical idea to SlotMachine)
        cardBounds.forEach(((upgrade, rectangle) -> {
            Slot cardSlot = cardAnimationManagers.get(upgrade);
            float s = cardSlot.scale
                * cardSlot.pulseScale()
                * cardSlot.wobbleScale();

            float drawW = rectangle.width  * s;
            float drawH = rectangle.height * s;

            // center scaling around the original x/y
            float adjX = rectangle.x - (drawW - rectangle.width) / 2f;
            float adjY = rectangle.y - (drawH - rectangle.height) / 2f;

            float rotation = cardSlot.wobbleAngleDeg();

            // draw shadow (also scaled and rotated)
            batch.setColor(1f, 1f, 1f, 0.25f);
            batch.draw(
                cardShadow,
                adjX + 0.1f, adjY - 0.1f,
                drawW / 2f, drawH / 2f,   // origin for rotation (center)
                drawW, drawH,
                1f, 1f,
                rotation
            );

            // draw card
            batch.setColor(1f, 1f, 1f, 1f);
            batch.draw(
                jokerCard,
                adjX, adjY,
                drawW / 2f, drawH / 2f,   // origin for rotation (center)
                drawW, drawH,
                1f, 1f,
                rotation
            );
        }));
    }

    public Upgrade getHoveringUpgrade() {
        return hoveringKey;
    }

    public Rectangle getHoveringRectangle() {
        return cardBounds.get(hoveringKey);
    }

    public Slot getSlotByUpgrade(Upgrade upgrade) {
        return cardAnimationManagers.get(upgrade);
    }

    public Rectangle getRectangleByUpgrade(Upgrade upgrade) {
        return cardBounds.get(upgrade);
    }

}
