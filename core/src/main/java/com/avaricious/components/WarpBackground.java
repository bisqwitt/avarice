package com.avaricious.components;

import com.avaricious.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class WarpBackground {

    private final ShaderProgram shader;
    private float time;

    private final Texture whiteTexture;

    public WarpBackground() {
        ShaderProgram.pedantic = false;

        whiteTexture = Assets.I().getWhiteTexture();
        shader = new ShaderProgram(
            Gdx.files.internal("backgroundShader/warp.vert"),
            Gdx.files.internal("backgroundShader/warp.frag")
        );
        if (!shader.isCompiled()) {
            throw new GdxRuntimeException("Shader compile error:\n" + shader.getLog());
        }
    }

    public void render(SpriteBatch batch, float delta) {
        time += delta;

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setShader(shader);
        batch.begin();

        shader.setUniformf("u_time", time);

        // Example: 64 blocks across screen → pixelSize = 1/64
        float blocks = 192f;
        float pixelSize = 1.0f / blocks;
        shader.setUniformf("u_pixelSize", pixelSize);

        // Draw full-screen quad / texture
        batch.draw(whiteTexture,
            0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.end();
        batch.setShader(null);
    }

//    public void renderOnTexture(SpriteBatch batch, float delta, Texture texture, Rectangle rectangle) {
//        time += delta;
//
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        batch.setShader(shader);
//        batch.begin();
//
//        shader.setUniformf("u_time", time);
//
//        // Example: 64 blocks across screen → pixelSize = 1/64
//        float blocks = 176f;
//        float pixelSize = 1.0f / blocks;
//        shader.setUniformf("u_pixelSize", pixelSize);
//
//        // Draw full-screen quad / texture
//        batch.draw(texture,
//            rectangle.x, rectangle.y,
//            rectangle.width, rectangle.height);
//
//        batch.end();
//        batch.setShader(null);
//    }
}
