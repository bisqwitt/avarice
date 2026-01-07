package com.avaricious;

import com.avaricious.components.DigitalNumber;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class CreditNumber extends DigitalNumber {

    private final Texture dollarSymbol;
    private final Texture dollarSymbolShadow;

    public CreditNumber(int initialScore, Rectangle rectangle, float offset) {
        super(initialScore, Assets.I().colorYellow(), rectangle, offset);

        dollarSymbol = Assets.I().getDollarSymbol();
        dollarSymbolShadow = Assets.I().getDollarSymbolShadow();
    }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        super.draw(batch, delta);

        float x = rectangle.x + (numberTextures.size() * offset) + 0.05f;
        float y = calcHoverY();

        batch.setColor(1f, 1f, 1f, 0.25f);
        batch.draw(dollarSymbolShadow, x + 0.05f, y - 0.05f, rectangle.width, rectangle.height);
        batch.setColor(color);
        batch.draw(dollarSymbol, x, y, rectangle.width, rectangle.height);
        batch.setColor(1f, 1f, 1f, 1f);
    }
}
