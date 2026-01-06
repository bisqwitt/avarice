package com.avaricious;

import com.avaricious.components.DigitalNumber;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class CreditNumber extends DigitalNumber {

    private final Texture dollarSymbol;

    public CreditNumber(int initialScore, Rectangle rectangle, float offset) {
        super(initialScore, Assets.I().colorYellow(), rectangle, offset);

        dollarSymbol = Assets.I().getDollarSymbol();
    }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        super.draw(batch, delta);
        batch.setColor(color);
        batch.draw(dollarSymbol, rectangle.x + (numberTextures.size() * offset) + 0.05f, calcHoverY(), rectangle.width, rectangle.height);
        batch.setColor(1f, 1f, 1f, 1f);
    }
}
