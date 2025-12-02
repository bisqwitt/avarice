package com.avaricious.screens;

import com.avaricious.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.badlogic.gdx.video.assets.VideoLoader;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.FisheyeEffect;
import com.crashinvaders.vfx.effects.MotionBlurEffect;
import com.crashinvaders.vfx.effects.OldTvEffect;
import com.crashinvaders.vfx.effects.util.MixEffect;

import java.io.FileNotFoundException;

public class MainScreen extends ScreenAdapter {

    private final Main app;
    private final VideoPlayer videoPlayer;

    private final VfxManager vfxManager;

    public MainScreen(Main app) {
        this.app = app;

        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
//        vfxManager.addEffect(new OldTvEffect());
        BloomEffect bloomEffect = new BloomEffect();
        bloomEffect.setBaseIntensity(1);
        bloomEffect.setBloomIntensity(3f);
        bloomEffect.setThreshold(0.4f);
        bloomEffect.setBlurAmount(1.25f);
        bloomEffect.setBlurPasses(5);
        vfxManager.addEffect(bloomEffect);
//        vfxManager.addEffect(new FisheyeEffect());
//        vfxManager.addEffect(new MotionBlurEffect(Pixmap.Format.RGBA8888, MixEffect.Method.MIX, 0.75f));

        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        FileHandle fh = Gdx.files.internal("video/flower-bloom.webm");
        try {
            videoPlayer.load(fh);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
//        videoPlayer.setLooping(true);
        videoPlayer.play();
    }

    @Override
    public void render(float delta) {
        videoPlayer.update();
        SpriteBatch batch = app.getBatch();
        app.getViewport().apply();
        batch.setProjectionMatrix(app.getViewport().getCamera().combined);

        Texture frame = videoPlayer.getTexture();
        if(frame != null) {
            vfxManager.cleanUpBuffers();
            vfxManager.beginInputCapture();

            batch.begin();
            if(videoPlayer.isPlaying()) {
                batch.draw(frame, 0f, 0f, 16f, 9f);
            } else {
                batch.draw(frame, 0f, 0f, 50f, 50f);
            }
            batch.end();

            vfxManager.endInputCapture();
            vfxManager.applyEffects();
            vfxManager.renderToScreen();
        }
    }
}
