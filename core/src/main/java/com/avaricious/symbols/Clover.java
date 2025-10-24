package com.avaricious.symbols;

import com.badlogic.gdx.assets.AssetManager;
import sun.jvm.hotspot.debugger.cdbg.Sym;

public class Clover extends Symbol {
    public Clover(AssetManager assetManager) {
        super(assetManager);
    }

    @Override
    protected String getPath() {
        return "clover.png";
    }
}
