package com.avaricious;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<ParticleEffect> dump = emitters.stream().filter(ParticleEffect::isComplete).collect(Collectors.toSet());
        emitters.remove(dump);
    }

    public void create(float x, float y, ParticleType type, float streak) {
        ParticleEffect particle = new ParticleEffect();
        particle.load(type.getFile(),
            Gdx.files.internal("particles/pngs"));
        particle.scaleEffect(0.03f);
        particle.getEmitters().forEach(emitter -> emitter.getEmission().setHigh(streak * 60));

        particle.setPosition(x, y);
        particle.start();
        emitters.add(particle);
    }

    public void create(float x, float y, ParticleType type) {
        ParticleEffect particle = new ParticleEffect();
        particle.load(type.getFile(),
            Gdx.files.internal("particles/pngs"));
        particle.scaleEffect(0.01f);

        particle.setPosition(x, y);
        particle.start();
        emitters.add(particle);
    }

}

