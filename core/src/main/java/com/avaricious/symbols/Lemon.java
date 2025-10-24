package com.avaricious.symbols;

import com.badlogic.gdx.assets.AssetManager;

public class Lemon extends Symbol {

    public Lemon(AssetManager assetManager) {
        super(assetManager);
    }

    @Override
    protected String getPath() {
        return "lemon.png";
    }
}
