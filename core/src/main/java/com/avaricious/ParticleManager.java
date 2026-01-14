package com.avaricious;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager {

    private static ParticleManager instance;

    public static ParticleManager I() {
        return instance == null ? instance = new ParticleManager() : instance;
    }

    private ParticleManager() {
    }

    private final List<ParticleEffect> emitters = new ArrayList<>();


    public void draw(SpriteBatch batch, float delta) {
        emitters.forEach(emitter -> {
            emitter.update(delta);
            emitter.draw(batch);
        });
    }

    public void create(float x, float y, ParticleType type, float streak) {
        ParticleEffect particle = new ParticleEffect();
        particle.load(type.getFile(),
            Gdx.files.internal("particles/pngs"));
        particle.scaleEffect(0.03f);
        particle.getEmitters().forEach(emitter -> emitter.getEmission().setHigh(streak * 25));

        particle.setPosition(x, y);
        particle.start();
        emitters.add(particle);
    }

}

