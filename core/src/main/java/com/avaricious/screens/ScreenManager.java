package com.avaricious.screens;

import com.avaricious.Main;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

public class ScreenManager {

    private static ScreenManager instance;

    private static ScreenViewport uiViewport;
    private static FitViewport viewport;

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
        screens.put(MainScreen.class, new MainScreen(app));
        screens.put(SlotScreen.class, new SlotScreen(app));
    }

    public void setScreen(Class<? extends ScreenAdapter> screenClass) {
        app.setScreen(screens.get(screenClass));
    }

    public static void setViewport(FitViewport fitViewport) {
        viewport = fitViewport;
    }

    public static FitViewport getViewport() {
        return viewport;
    }

    public static void setUiViewport(ScreenViewport viewport) {
        uiViewport = viewport;
    }

    public static Viewport getUiViewport() {
        return uiViewport;
    }

}
