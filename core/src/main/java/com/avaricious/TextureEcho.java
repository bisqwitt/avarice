package com.avaricious;

import com.avaricious.stats.statupgrades.EvadeChance;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class TextureEcho {

    private final TextureRegion textureEcho;
    private final Color color;

    private final Rectangle currentPos;
    private final float streak;
    private float alpha = 0.5f;

    private static final List<TextureEcho> echos = new ArrayList<>();

    public static void create(TextureRegion texture, Rectangle spawnPoint, Color color, float streak) {
        echos.add(new TextureEcho(texture, spawnPoint, color, streak));
    }

    public static void draw(SpriteBatch batch, float delta) {
        echos.forEach(echo -> echo._draw(batch, delta));
        echos.removeIf(echo -> !echo.isAlive());
    }

    private TextureEcho(TextureRegion textureEcho, Rectangle spawnPoint, Color color, float streak) {
        this.textureEcho = textureEcho;
        this.color = color;
        currentPos = new Rectangle(spawnPoint);
        this.streak = streak;
    }

    private void _draw(SpriteBatch batch, float delta) {
        batch.setColor(color.r, color.g, color.b, alpha);
        batch.draw(textureEcho, currentPos.x, currentPos.y, currentPos.width, currentPos.height);
        batch.setColor(1f, 1f, 1f, 1f);

        float echoSpeed = 18f;
        float fadeSpeed = 1f;

        float additionalSize = delta * echoSpeed;
        float aspectRatio = currentPos.width / currentPos.height;

        currentPos.width += additionalSize;
        currentPos.height = currentPos.width / aspectRatio;
        currentPos.x -= additionalSize / 2;
        currentPos.y -= additionalSize / 2;

        alpha -= delta * fadeSpeed;
    }

    public boolean isAlive() {
        return alpha > 0f;
    }
}

