package com.avaricious.upgrades.bars;

import com.avaricious.Assets;
import com.avaricious.upgrades.Upgrade;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

public class JokerUpgradeBarWithPrices extends JokerUpgradeBar {

    private final TextureRegion jokerCardPriceBox;

    public JokerUpgradeBarWithPrices(List<? extends Upgrade> upgrades, Rectangle cardRectangle, float offset, boolean tooltipOnTop) {
        super(upgrades, cardRectangle, offset, tooltipOnTop);
        jokerCardPriceBox = new TextureRegion(Assets.I().getJokerCardPriceBox());
    }

    @Override
    protected void drawCard(SpriteBatch batch, Upgrade upgrade, Rectangle bounds, float scale, float rotation) {
        super.drawCard(batch, upgrade, bounds, scale, rotation);

        float priceBoxWidth = (74 / 130f) * scale;
        float priceBoxHeight = (43 / 130f) * scale;

        batch.draw(
            jokerCardPriceBox,
            bounds.x + 0.285f, bounds.y + bounds.height,
            priceBoxWidth / 2f, priceBoxHeight / 2f,
            priceBoxWidth, priceBoxHeight,
            1f, 1f,
            rotation
        );
    }
}
