package com.avaricious.components;

import com.avaricious.Assets;
import com.avaricious.components.popups.NumberPopup;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.Slot;   // <-- import your Slot
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.StatUpgrade;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeBar {

    private TextureRegion jokerCard;
    private final TextureRegion cardShadow;

    private final Rectangle cardRectangle;
    private final float offset;

    private final Map<Upgrade, Rectangle> cardBounds = new HashMap<>();
    private final Map<Upgrade, Slot> cardAnimationManagers = new HashMap<>();

    private Upgrade hoveringKey = null;

    private final boolean tooltipOnTopOfCard;

    private Runnable onUpgradeClicked;

    public UpgradeBar(List<? extends Upgrade> upgrades, Rectangle cardRectangle, float offset, boolean tooltipOnTop) {
        tooltipOnTopOfCard = tooltipOnTop;
        jokerCard = new TextureRegion(Assets.I().getJokerCard());
        cardShadow = new TextureRegion(Assets.I().getJokerCardShadow());

        this.cardRectangle = cardRectangle;
        this.offset = offset;
        loadUpgrades(upgrades);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        hoveringKey = null;
        Upgrade[] clickedUpgrade = new Upgrade[1];
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
            if(selected) clickedUpgrade[0] = hoveringKey;
        }));

        if(hoveringKey != null)
            PopupManager.I().showTooltip(hoveringKey,
                getHoveringRectangle().x - 1f, getHoveringRectangle().y + (tooltipOnTopOfCard ? 2 : -2));

        if(clickedUpgrade[0] != null) {
            if(clickedUpgrade[0] instanceof StatUpgrade) {
                clickedUpgrade[0].apply();
                cardBounds.keySet().retainAll(Collections.singleton(clickedUpgrade[0]));
                cardAnimationManagers.keySet().retainAll(Collections.singleton(clickedUpgrade[0]));

                PopupManager.I().spawnPercentage(
                    ((StatUpgrade) clickedUpgrade[0]).getStat().getPercentageAsNumber(),
                    Assets.I().colorGreen(),
                    cardBounds.get(clickedUpgrade[0]).getX() + 1f,
                    cardBounds.get(clickedUpgrade[0]).getY()).setOnFinished(() -> onUpgradeClicked.run());
            } else {
                UpgradesManager.I().addUpgrade(clickedUpgrade[0]);
                cardBounds.remove(clickedUpgrade[0]);
                cardAnimationManagers.remove(clickedUpgrade[0]);
                hoveringKey = null;
            }
        }
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
                upgrade instanceof StatUpgrade ? new TextureRegion(((StatUpgrade) upgrade).getStat().getShadowTexture()) : cardShadow,
                adjX + 0.1f, adjY - 0.1f,
                drawW / 2f, drawH / 2f,   // origin for rotation (center)
                drawW, drawH,
                1f, 1f,
                rotation
            );

            // draw card
            batch.setColor(1f, 1f, 1f, 1f);
            batch.draw(
                upgrade instanceof StatUpgrade ? new TextureRegion(((StatUpgrade) upgrade).getStat().getTexture()) : jokerCard,
                adjX, adjY,
                drawW / 2f, drawH / 2f,   // origin for rotation (center)
                drawW, drawH,
                1f, 1f,
                rotation
            );
        }));
    }

    public void loadUpgrades(List<? extends Upgrade> upgrades) {
        cardBounds.clear();
        cardAnimationManagers.clear();

        for(int i = 0; i < upgrades.size(); i++) {
            Upgrade upgrade = upgrades.get(i);
            cardBounds.put(upgrade, new Rectangle(cardRectangle.x + (i * offset), cardRectangle.y, cardRectangle.width, cardRectangle.height));
            cardAnimationManagers.put(upgrade, new Slot(new Vector2(cardRectangle.x, cardRectangle.y)));
        }
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

    public void setOnUpgradeClicked(Runnable onUpgradeClicked) {
        this.onUpgradeClicked = onUpgradeClicked;
    }
}
