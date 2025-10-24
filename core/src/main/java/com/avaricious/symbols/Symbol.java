package com.avaricious.symbols;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public abstract class Symbol {

    protected AssetManager assetManager;

    public Symbol(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Texture getTexture() {
        return assetManager.get(getPath(), Texture.class);
    }

    abstract String getPath();

}
