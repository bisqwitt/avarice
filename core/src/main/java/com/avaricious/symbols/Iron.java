package com.avaricious.symbols;

import com.badlogic.gdx.assets.AssetManager;

public class Iron extends Symbol{
    public Iron(AssetManager assetManager) {
        super(assetManager);
    }

    @Override
    protected String getPath() {
        return "iron.png";
    }
}
