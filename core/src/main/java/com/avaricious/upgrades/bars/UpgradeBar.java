package com.avaricious.upgrades.bars;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.ObjectWithPopEffect;   // <-- import your Slot
import com.avaricious.upgrades.Upgrade;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UpgradeBar {

    private final Rectangle cardRectangle;
    private final float offset;
    private final boolean tooltipOnTopOfCard;

    protected final Map<Upgrade, ObjectWithPopEffect> jokerCards = new HashMap<>();

    protected Runnable onUpgradeClicked;
    protected Upgrade hoveredUpgrade;

    public UpgradeBar(List<? extends Upgrade> upgrades, Rectangle cardRectangle, float offset, boolean tooltipOnTop) {
        tooltipOnTopOfCard = tooltipOnTop;

        this.cardRectangle = cardRectangle;
        this.offset = offset;
        loadUpgrades(upgrades);
    }

    public void handleInput(Vector2 mouse, boolean mouseDown, boolean mouseWasDown, float delta) {
        Upgrade clickedUpgrade = null;

        for(Map.Entry<Upgrade, ObjectWithPopEffect> jokerCards : jokerCards.entrySet()) {
            Upgrade upgrade = jokerCards.getKey();
            ObjectWithPopEffect jokerCard =  jokerCards.getValue();

            boolean hovered = jokerCard.getBounds().contains(mouse);
            boolean hoveredEntry = hoveredUpgrade == null && hovered;
            boolean pressed = mouseDown && hovered;
            boolean pressedEntry = pressed && !mouseWasDown;
            boolean clicked = hovered && mouseWasDown && !mouseDown;

            jokerCard.targetScale = pressed ? 1.2f
                : hovered ? 1.075f : 1f;

            if(hoveredEntry) {
                jokerCard.wobble();
                jokerCard.pulse();
            }

            jokerCard.handleInput(delta);

            if(hovered) {
                hoveredUpgrade = upgrade;
                PopupManager.I().renderTooltip(upgrade,
                    jokerCard.getBounds().x - 1f, jokerCard.getBounds().y + (tooltipOnTopOfCard ? 2 : -2));
            }

            if(clicked) onCardClicked(upgrade);
        }
    }

    /**
     * Draw the card using Slot's wobble/scale.
     */
    public void draw(SpriteBatch batch) {
        // combined scale (identical idea to SlotMachine)
        jokerCards.forEach(((upgrade, jokerCard) -> {
            float s = jokerCard.scale
                * jokerCard.pulseScale()
                * jokerCard.wobbleScale();

            float drawW = jokerCard.getBounds().width  * s;
            float drawH = jokerCard.getBounds().height * s;

            // center scaling around the original x/y
            float adjX = jokerCard.getBounds().x - (drawW - jokerCard.getBounds().width) / 2f;
            float adjY = jokerCard.getBounds().y - (drawH - jokerCard.getBounds().height) / 2f;

            float rotation = jokerCard.wobbleAngleDeg();

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
        }));
    }

    protected void drawCard(SpriteBatch batch, Upgrade upgrade, Rectangle bounds, float scale, float rotation) {
    }

    public void loadUpgrades(List<? extends Upgrade> upgrades) {
        cardBounds.clear();
        jokerCards.clear();

        for(int i = 0; i < upgrades.size(); i++) {
            Upgrade upgrade = upgrades.get(i);
            jokerCards.put(upgrade, new ObjectWithPopEffect(new Rectangle(cardRectangle.x + (i * offset), cardRectangle.y, cardRectangle.width, cardRectangle.height)));
        }
    }

    protected abstract void onCardClicked(Upgrade clickedUpgrade);

    protected abstract TextureRegion getTexture(Upgrade upgrade);

    protected abstract TextureRegion getShadow(Upgrade upgrade);

    public Rectangle getHoveringRectangle() {

    }

    public ObjectWithPopEffect getSlotByUpgrade(Upgrade upgrade) {
        return jokerCards.get(upgrade);
    }

    public Rectangle getRectangleByUpgrade(Upgrade upgrade) {
        return cardBounds.get(upgrade);
    }

    public void setOnUpgradeClickedAndAnimationEnded(Runnable onUpgradeClicked) {
        this.onUpgradeClicked = onUpgradeClicked;
    }
}
