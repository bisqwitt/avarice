package com.avaricious;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public enum ParticleType {
    RAINBOW(Gdx.files.internal("particles/rainbow-pixel-particle.p")),
    BLUE(Gdx.files.internal("particles/blue-pixel-particle.p")),
    RED(Gdx.files.internal("particles/red-pixel-particle.p")),
    XP(Gdx.files.internal("particles/xp-particle.p"));

    private final FileHandle file;

    ParticleType(FileHandle file) {
        this.file = file;
    }

    public FileHandle getFile() {
        return file;
    }
}
