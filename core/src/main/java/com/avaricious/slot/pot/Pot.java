package com.avaricious.slot.pot;

import com.avaricious.Assets;
import com.avaricious.Main;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

// ... imports unchanged ...
public class Pot {

    private final int cells = 7;
    private final float cellW = 1.75f;
    private final float cellH = 1.75f;

    private final float originX;
    private final float originY;

    private final UpgradesManager upgradesManager;
    private final TextureRegion chipTexture;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    private int hoverIndex = -1;

    private final List<GlyphLayout> descriptions = new ArrayList<>();
    private final List<Chip> chips = new ArrayList<>(); // NEW per-chip state

    public Pot(float worldWidth, float worldHeight) {
        originX = (worldWidth - cells * cellW) / 2f;
        originY = 0.5f;

        shapeRenderer = new ShapeRenderer();
        upgradesManager = UpgradesManager.I();
        chipTexture = new TextureRegion(Assets.I().getPokerChip());
        font = Assets.I().getSmallFont();

        for (int i = 0; i < cells; i++) {
            descriptions.add(new GlyphLayout());
            chips.add(new Chip()); // init chip states
        }
    }

    public void draw(Main app, float delta) {
        SpriteBatch batch = app.getBatch();
        var upgrades = upgradesManager.getUpgrades();

        // ----- WORLD draw (chips) -----
        app.getViewport().apply();
        batch.setProjectionMatrix(app.getViewport().getCamera().combined);
        batch.begin();

        for (int i = 0; i < upgrades.size(); i++) {
            boolean hovered = (i == hoverIndex);
            Chip chip = chips.get(i);

            // drive animation
            chip.targetScale = hovered ? 1.125f : 1f;
            chip.updateHoverWobble(hovered, delta);
            chip.tickScale(delta);

            float s = chip.scale * chip.wobbleScale();

            float baseX = (originX + i * cellW) + 0.25f;
            float baseY = (originY) + 0.3f;

            float drawW = 1f * s;              // chip is 1x1 world units in your code
            float drawH = 1f * s;
            float adjX = baseX - (drawW - 1f) / 2f;
            float adjY = baseY - (drawH - 1f) / 2f;

            float rotation = chip.wobbleAngleDeg(); // rotate around center

            batch.draw(
                chipTexture,
                adjX, adjY,
                drawW / 2f, drawH / 2f,   // origin for rotation = center
                drawW, drawH,
                1f, 1f,
                rotation
            );
        }
        batch.end();

        // ----- UI draw (tooltip) -----
        if (hoverIndex != -1) {
            var upgradesList = upgrades; // alias
            float chipX = originX + hoverIndex * cellW;
            float chipY = originY + cellH - 0.5f;

            // background (in world space, behind tooltip if you like)
            shapeRenderer.setProjectionMatrix(app.getViewport().getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.LIGHT_GRAY);
            shapeRenderer.rect(chipX, chipY, 2f, 1.75f);
            shapeRenderer.end();

            // layout wrapped text (150 px wrap width in UI coords)
            GlyphLayout description = descriptions.get(hoverIndex);
            description.setText(
                font,
                upgradesList.get(hoverIndex).description(),
                Color.WHITE,
                180f,
                Align.center,
                true
            );

            Vector2 uiPos = worldToUI(app, chipX, chipY);

            app.getUiViewport().apply();
            batch.setProjectionMatrix(app.getUiViewport().getCamera().combined);
            batch.begin();
            font.draw(batch, description, uiPos.x, uiPos.y - 350f); // adjust Y as needed
            batch.end();
        }
    }

    public void hoveringAt(Vector3 mouse) {
        var upgrades = upgradesManager.getUpgrades();
        hoverIndex = -1;
        for (int i = 0; i < upgrades.size(); i++) {
            float x = (originX + i * cellW) + 0.25f;
            float y = (originY) + 0.3f;
            if (new Rectangle(x, y, 1f, 1f).contains(mouse.x, mouse.y)) {
                hoverIndex = i;
                return;
            }
        }
    }

    private Vector2 worldToUI(Main app, float worldX, float worldY) {
        Vector3 v = new Vector3(worldX, worldY, 0);
        app.getViewport().getCamera().project(v);
        app.getUiViewport().getCamera().unproject(v);
        return new Vector2(v.x, v.y);
    }

    public Rectangle getBounds() {
        return new Rectangle(originX, originY, cellW * cells, cellH);
    }
}
