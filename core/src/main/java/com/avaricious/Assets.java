package com.avaricious;

import com.avaricious.slot.Symbol;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import java.util.Arrays;

public class Assets {

    private static Assets instance;
    public static Assets I() {
        return instance == null ? (instance = new Assets()) : instance;
    }

    private final AssetManager manager = new AssetManager();

    private Assets() {
    }

    public void load() {
        Arrays.asList(Symbol.values()).forEach((symbol) -> {
            manager.load(symbol.path(), Texture.class);
        });
        manager.finishLoading();
    }

    public Texture get(Symbol type) {
        return manager.get(type.path());
    }

}
