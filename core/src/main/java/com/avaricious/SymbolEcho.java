package com.avaricious;

import com.avaricious.components.slot.Symbol;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class SymbolEcho {

    public static SymbolEcho spawn(TextureRegion symbolTexture, Rectangle spawnPoint) {
        return new SymbolEcho(symbolTexture, spawnPoint);
    }

    private final TextureRegion symbolTexture;

    private final Rectangle currentPos;
    private float alpha = 1f;

    public SymbolEcho(TextureRegion symbolTexture, Rectangle spawnPoint) {
        this.symbolTexture = symbolTexture;
        currentPos = spawnPoint;
    }

    public void draw(SpriteBatch batch, float delta) {
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(symbolTexture, currentPos.x, currentPos.y, currentPos.width, currentPos.height);
        batch.setColor(1f, 1f, 1f, 1f);

        float echoSpeed = 1f;
        float fadeSpeed = 1f;

        currentPos.width += delta * echoSpeed;
        alpha += delta * fadeSpeed;
    }

}
