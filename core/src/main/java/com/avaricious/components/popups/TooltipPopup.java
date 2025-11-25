package com.avaricious.components.popups;

import com.avaricious.Assets;
import com.avaricious.screens.ScreenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class TooltipPopup {

    private final GlyphLayout description = new GlyphLayout();
    private final Rectangle rectangle;
    private final Texture box;
    private final BitmapFont bigFont;
    private final BitmapFont smallFont;

    public TooltipPopup(String txt, Rectangle card) {
        rectangle = card;
        box = Assets.I().getTooltipBox();
        bigFont = Assets.I().getBigFont();
        smallFont = Assets.I().getSmallFont();
        description.setText(smallFont, "[BLACK]" + txt + "[]", Color.WHITE, 250f, Align.top | Align.center, true);
    }

    public void render(SpriteBatch batch) {
        float boxWidth = 82 / 25f;
        float boxHeight = 41 / 25f;
        float boxX = rectangle.x - 1f;
        float boxY = rectangle.y + 2f;

        // 1) Draw box in WORLD space
        batch.setProjectionMatrix(ScreenManager.getViewport().getCamera().combined);
        batch.draw(box, boxX, boxY, boxWidth, boxHeight);

        // 2) Project box center to screen coordinates
        Vector2 center = new Vector2(boxX + boxWidth / 2f, boxY + boxHeight / 2f);
        ScreenManager.getViewport().project(center); // world -> screen

        // 3) Switch to UI viewport for text
        batch.setProjectionMatrix(ScreenManager.getUiViewport().getCamera().combined);
        bigFont.draw(batch, "Joker", 327f + (rectangle.x * 90) - 300f, 362f);

        float textW = description.width;
        float textH = description.height;

        float textX = center.x - textW / 2f;
        float textY = center.y + textH / 2f;

        smallFont.draw(batch, description, textX - 30f, textY);

        // 4) (If needed) restore world projection for later world drawing
        batch.setProjectionMatrix(ScreenManager.getViewport().getCamera().combined);
    }



}
