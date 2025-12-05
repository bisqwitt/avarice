package com.avaricious.components;

import com.avaricious.Assets;
import com.avaricious.components.progressbar.ProgressBar;
import com.avaricious.components.slot.Symbol;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Arrays;

public class SymbolProgress {

    private final TextureRegion[] symbolTextures;
    private final ProgressBar[] progressBars = new ProgressBar[7];

    public SymbolProgress() {
        symbolTextures = Arrays.stream(Symbol.values()).map(symbol -> Assets.I().getBase(symbol)).toArray(TextureRegion[]::new);
    }

    public void draw(SpriteBatch batch) {

    }
}
