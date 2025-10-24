package com.avaricious;

import com.badlogic.gdx.assets.AssetManager;

public class Assets {

    private static Assets instance;
    public static Assets I() {
        return instance == null ? (instance = new Assets()) : instance;
    }

    public final AssetManager manager = new AssetManager();

    private Assets() {
    }

    public void load() {
        manager.load();
    }

}
