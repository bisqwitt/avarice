package com.avaricious.symbols;

import com.badlogic.gdx.assets.AssetManager;

public class Cherry extends Symbol {
    public Cherry(AssetManager assetManager) {
        super(assetManager);
    }

    @Override
    protected String getPath() {
        return "cherry.png";
    }
}
