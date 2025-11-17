package com.avaricious;

import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    SpriteBatch batch;
    FitViewport viewport;
    ScreenViewport uiViewport;

    @Override
    public void create() {
        Assets.I().load();

        batch = new SpriteBatch();
        viewport = new FitViewport(16, 9);
        uiViewport = new ScreenViewport();
        ScreenManager.create(this).setScreen(SlotScreen.class);
    }

    public FitViewport getViewport() {
        return viewport;
    }

    public ScreenViewport getUiViewport() {
        return uiViewport;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
