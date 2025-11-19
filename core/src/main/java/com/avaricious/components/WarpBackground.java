package com.avaricious.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class WarpBackground {

    private final ShaderProgram shader;
    private final Mesh mesh;
    private float time;

    public WarpBackground() {
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(
            Gdx.files.internal("backgroundShader/warp.vert"),
            Gdx.files.internal("backgroundShader/warp.frag")
        );
        if (!shader.isCompiled()) {
            throw new GdxRuntimeException("Shader compile error:\n" + shader.getLog());
        }

        mesh = new Mesh(
            true,
            4, 6,
            new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position")
        );

        float[] verts = new float[] {
            -1f, -1f,
            1f, -1f,
            1f,  1f,
            -1f,  1f
        };
        short[] indices = new short[] { 0, 1, 2, 2, 3, 0 };

        mesh.setVertices(verts);
        mesh.setIndices(indices);
    }

    public void render(float delta) {
        time += delta;

        shader.bind();
        shader.setUniformf("u_time", time);
        mesh.render(shader, GL20.GL_TRIANGLES);
    }

    public void dispose() {
        mesh.dispose();
        shader.dispose();
    }
}
