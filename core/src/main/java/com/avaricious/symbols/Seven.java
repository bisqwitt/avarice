package com.avaricious.symbols;

import com.badlogic.gdx.assets.AssetManager;

public class Seven extends Symbol{
    public Seven(AssetManager assetManager) {
        super(assetManager);
    }

    @Override
    protected String getPath() {
        return "seven.png";
    }
}
