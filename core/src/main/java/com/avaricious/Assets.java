package com.avaricious;

import com.avaricious.slot.Symbol;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Arrays;

public class Assets {

    private static Assets instance;
    public static Assets I() {
        return instance == null ? (instance = new Assets()) : instance;
    }

    private final AssetManager manager = new AssetManager();

    private TextureAtlas atlas;
    private ObjectMap<Symbol, TextureRegion> baseMap = new ObjectMap<>();
    private ObjectMap<Symbol, Animation<TextureAtlas.AtlasRegion>> borderMap = new ObjectMap<>();

    private BitmapFont bigFont;
    private BitmapFont smallFont;

    private Assets() {
    }

    public void load() {
        manager.load("SlotMachineBorder.png", Texture.class);
        manager.load("base-poker-chip.png", Texture.class);

        atlas = new TextureAtlas(Gdx.files.internal("symbols.atlas"));
        atlas.getTextures().forEach(texture -> texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest));

        Arrays.asList(Symbol.values()).forEach(symbol -> {
            String name = symbol.name().toLowerCase() + "/" + symbol.name().toLowerCase();
            baseMap.put(symbol, atlas.findRegion(name));

            Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(name + "_border");
            Animation<TextureAtlas.AtlasRegion> anim = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
            borderMap.put(symbol, anim);
        });

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelifySans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 56;
        bigFont = generator.generateFont(param);
        bigFont.setUseIntegerPositions(false);
        bigFont.getData().markupEnabled = true;
        param.size = 26;
        smallFont = generator.generateFont(param);
        smallFont.setUseIntegerPositions(false);
        smallFont.getData().markupEnabled = true;
        generator.dispose();

        manager.finishLoading();
    }

    public TextureRegion getBase(Symbol s) {
        return baseMap.get(s);
    }

    public Animation<TextureAtlas.AtlasRegion> getBorderAnimation(Symbol s) {
        return borderMap.get(s);
    }

    public Texture getSlotMachineBorder() {
        return manager.get("SlotMachineBorder.png", Texture.class);
    }

    public Texture getPokerChip() {
        return manager.get("base-poker-chip.png", Texture.class);
    }

    public String colorBlue(String txt) {
        return "[#2A8AC8]" + txt + "[]";
    }

    public String colorBlue(long l) {
        return colorBlue(String.valueOf(l));
    }

    public String colorRed(String txt) {
        return "[#CA6055]" + txt + "[]";
    }

    public BitmapFont getBigFont() {
        return bigFont;
    }

    public BitmapFont getSmallFont() {
        return smallFont;
    }

    public String colorRed(long l) {
        return colorRed(String.valueOf(l));
    }

}
