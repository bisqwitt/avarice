package com.avaricious.upgrades.bars;

import com.avaricious.Assets;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.Slot;   // <-- import your Slot
import com.avaricious.stats.statupgrades.StatUpgrade;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public abstract class UpgradeBar {

    private final Rectangle cardRectangle;
    private final float offset;

    protected final Map<Upgrade, Rectangle> cardBounds = new HashMap<>();
    protected final Map<Upgrade, Slot> cardAnimationManagers = new HashMap<>();

    private Upgrade hoveringKey = null;

    private final boolean tooltipOnTopOfCard;

    protected Runnable onUpgradeClicked;

    public UpgradeBar(List<? extends Upgrade> upgrades, Rectangle cardRectangle, float offset, boolean tooltipOnTop) {
        tooltipOnTopOfCard = tooltipOnTop;

        this.cardRectangle = cardRectangle;
        this.offset = offset;
        loadUpgrades(upgrades);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        hoveringKey = null;
        Upgrade[] clickedUpgrade = new Upgrade[1];
        cardBounds.forEach(((upgrade, rectangle) -> {
            boolean hovered = rectangle.contains(mouse);
            boolean selected = wasPressed && !pressed && rectangle.contains(mouse);

            Slot cardSlot = cardAnimationManagers.get(upgrade);
            cardSlot.targetScale = pressed && rectangle.contains(mouse) ? 1.2f
                : hovered ? 1.075f : 1f;

            cardSlot.updatePulse(false, delta);
            cardSlot.updateHoverWobble(hovered, delta);
            cardSlot.tickScale(delta);

            if(hovered) hoveringKey = upgrade;
            if(selected) clickedUpgrade[0] = hoveringKey;
        }));

        if(hoveringKey != null)
            PopupManager.I().showTooltip(hoveringKey,
                getHoveringRectangle().x - 1f, getHoveringRectangle().y + (tooltipOnTopOfCard ? 2 : -2));

        if(clickedUpgrade[0] != null) {
            onCardClicked(clickedUpgrade[0]);
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

            drawCard(batch, upgrade, new Rectangle(adjX, adjY, drawW, drawH), s, rotation);
        }));
    }

    protected void drawCard(SpriteBatch batch, Upgrade upgrade, Rectangle bounds, float scale, float rotation) {
        // draw shadow (also scaled and rotated)
        batch.setColor(1f, 1f, 1f, 0.25f);
        batch.draw(
            getShadow(upgrade),
            bounds.x + 0.1f, bounds.y - 0.1f,
            bounds.width / 2f, bounds.height / 2f,   // origin for rotation (center)
            bounds.width, bounds.height,
            1f, 1f,
            rotation
        );

        // draw card
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(
            getTexture(upgrade),
            bounds.x, bounds.y,
            bounds.width / 2f, bounds.height / 2f,   // origin for rotation (center)
            bounds.width, bounds.height,
            1f, 1f,
            rotation
        );
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

    protected abstract void onCardClicked(Upgrade clickedUpgrade);

    protected abstract TextureRegion getTexture(Upgrade upgrade);

    protected abstract TextureRegion getShadow(Upgrade upgrade);

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

    public void setOnUpgradeClickedAndAnimationEnded(Runnable onUpgradeClicked) {
        this.onUpgradeClicked = onUpgradeClicked;
    }
}
