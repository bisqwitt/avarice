package com.avaricious.symbols;

import com.badlogic.gdx.assets.AssetManager;

public class Bell extends Symbol {
    public Bell(AssetManager assetManager) {
        super(assetManager);
    }

    @Override
    protected String getPath() {
        return "bell.png";
    }
}
