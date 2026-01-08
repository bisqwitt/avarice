package com.avaricious.upgrades.bars;

import com.avaricious.Assets;
import com.avaricious.CreditNumber;
import com.avaricious.upgrades.Upgrade;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class JokerUpgradeBarWithPrices extends JokerUpgradeBar {

    private final List<CreditNumber> cardPrices = new ArrayList<>();

    public JokerUpgradeBarWithPrices(List<? extends Upgrade> upgrades, Rectangle cardRectangle, float offset, boolean tooltipOnTop) {
        super(upgrades, cardRectangle, offset, tooltipOnTop);

        for(int i = 0; i < upgrades.size(); i++) {
            cardPrices.add(new CreditNumber(3,
                new Rectangle(cardRectangle.x + 0.35f + (i * 2f), cardRectangle.y + 2.1f, 0.32f / 1.25f, 0.56f / 1.25f), 0.35f));
        }
//        upgrades.forEach(() -> cardPrices.add(new CreditNumber()));
    }

    @Override
    protected void drawCard(SpriteBatch batch, Upgrade upgrade, Rectangle bounds, float scale, float rotation) {
        super.drawCard(batch, upgrade, bounds, scale, rotation);
        cardPrices.forEach(price -> price.draw(batch, 0f));
    }
}
