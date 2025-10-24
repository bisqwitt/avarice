// BetBar.java
package com.avaricious;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class BetBar {
    private final Symbols[] symbols = Symbols.values();
    private final int[] bets = new int[symbols.length];
    private final Rectangle[] hit = new Rectangle[symbols.length];

    // layout in WORLD units
    private final float x, y, width, height;
    private boolean wasPressed = false;
    private boolean wasRight = false;

    private final GlyphLayout layout = new GlyphLayout();

    public BetBar(float x, float y, float width, float height) {
        this.x = x; this.y = y; this.width = width; this.height = height;

        float cellW = width / symbols.length;

        for (int i = 0; i < symbols.length; i++) {
            float cx = x + i * cellW;
            hit[i] = new Rectangle(cx, y, cellW, height);
        }
    }

    /** Call every frame BEFORE draw() */
    public void handleInput(float worldMouseX, float worldMouseY) {
        boolean left = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        boolean right = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);

        for (int i = 0; i < hit.length; i++) {
            if (hit[i].contains(worldMouseX, worldMouseY)) {
                // increment once per click
                if (left && !wasPressed) bets[i] = Math.min(bets[i] + 1, 99);
                // optional: right-click to decrement
                if (right && !wasRight)  bets[i] = Math.max(bets[i] - 1, 0);
            }
        }
        wasPressed = left;
        wasRight   = right;
    }

    /** Draw panel, icons, and counters */
    public void draw(SpriteBatch batch, ShapeRenderer shapes) {
        // background bar (optional)
        if (shapes != null) {
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            shapes.setColor(0.12f, 0.12f, 0.12f, 1f);
            shapes.rect(x, y, width, height);
            shapes.end();
        }

        float cellW = width / symbols.length;
        float cellH = height;

        batch.begin();
        for (int i = 0; i < symbols.length; i++) {
            float cx = x + i * cellW;
            Rectangle r = hit[i];

            // icon sizing (leave room for text)
            float iconH = cellH * 0.62f;
            // inner padding per cell
            float pad = 0.1f;
            float iconW = cellW - pad * 2f;
            // keep aspect by fitting into iconW x iconH box
            Texture tex = Assets.I().get(symbols[i]);
            // center icon
            float ix = cx + (cellW - iconW) / 2f;
            float iy = y + cellH - iconH - pad;
            batch.draw(tex, ix, iy, iconW, iconH);

            // number text (centered under icon)
//            String text = String.valueOf(bets[i]);
//            layout.setText(font, text);
//            float tx = cx + cellW / 2f - layout.width / 2f;
//            float ty = y + pad + layout.height; // baseline
//            font.setColor(Color.WHITE);
//            font.draw(batch, layout, tx, ty);
        }
        batch.end();
    }

    public int[] bets() { return bets; }

    public int totalBet() {
        int t = 0; for (int b : bets) t += b; return t;
    }
}
