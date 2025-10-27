package com.avaricious.screens;

import com.avaricious.Main;
import com.badlogic.gdx.ScreenAdapter;

import java.util.HashMap;
import java.util.Map;

public class ScreenManager {

    private static ScreenManager instance;

    public static ScreenManager create(Main app) {
        return (instance = new ScreenManager(app));
    }

    public static ScreenManager I() {
        return instance;
    }

    private final Main app;

    private final Map<Class<? extends ScreenAdapter>, ScreenAdapter> screens = new HashMap<>();

    private ScreenManager(Main app) {
        this.app = app;
        screens.put(SlotScreen.class, new SlotScreen(app));
        screens.put(UpgradeSelectionScreen.class, new UpgradeSelectionScreen(app));
    }

    public void setScreen(Class<? extends ScreenAdapter> screenClass) {
        app.setScreen(screens.get(screenClass));
    }

}
