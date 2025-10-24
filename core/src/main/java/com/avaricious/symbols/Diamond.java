package com.avaricious.symbols;

import com.badlogic.gdx.assets.AssetManager;

public class Diamond extends Symbol {
    public Diamond(AssetManager assetManager) {
        super(assetManager);
    }

    @Override
    protected String getPath() {
        return "diamond.png";
    }
}
