package com.avaricious;

import com.avaricious.components.slot.Symbol;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
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
        manager.load("SlotMachineScreen.png", Texture.class);
        manager.load("ScoreBorder.png", Texture.class);
        manager.load("ButtonsLeftDisplay.png", Texture.class);
        manager.load("base-poker-chip.png", Texture.class);
        manager.load("PatternDisplay.png", Texture.class);

        manager.load("digital-numbers/unlit.png", Texture.class);
        manager.load("digital-numbers/0.png", Texture.class);
        manager.load("digital-numbers/1.png", Texture.class);
        manager.load("digital-numbers/2.png", Texture.class);
        manager.load("digital-numbers/3.png", Texture.class);
        manager.load("digital-numbers/4.png", Texture.class);
        manager.load("digital-numbers/5.png", Texture.class);
        manager.load("digital-numbers/6.png", Texture.class);
        manager.load("digital-numbers/7.png", Texture.class);
        manager.load("digital-numbers/8.png", Texture.class);
        manager.load("digital-numbers/9.png", Texture.class);

        manager.load("sticks/uncommon-stick.png", Texture.class);
        manager.load("sticks/common-stick.png", Texture.class);
        manager.load("sticks/rare-stick.png", Texture.class);
        manager.load("sticks/epic-stick.png", Texture.class);
        manager.load("sticks/legendary-stick.png", Texture.class);

        manager.load("buttons/button-board.png", Texture.class);
        manager.load("buttons/apply-button.png", Texture.class);
        manager.load("buttons/apply-button-pressed.png", Texture.class);
        manager.load("buttons/spin-button.png", Texture.class);
        manager.load("buttons/spin-button-pressed.png", Texture.class);

        manager.load("lemon.png", Texture.class);
        manager.load("cherry.png", Texture.class);
        manager.load("clover.png", Texture.class);
        manager.load("bell.png", Texture.class);
        manager.load("iron.png", Texture.class);
        manager.load("diamond.png", Texture.class);
        manager.load("seven.png", Texture.class);

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
        param.size = 66;
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
        if(s == Symbol.LEMON) return new TextureRegion(manager.get("lemon.png", Texture.class));
        if(s == Symbol.CHERRY) return new TextureRegion(manager.get("cherry.png", Texture.class));
        if(s == Symbol.CLOVER) return new TextureRegion(manager.get("clover.png", Texture.class));
        if(s == Symbol.BELL) return new TextureRegion(manager.get("bell.png", Texture.class));
        if(s == Symbol.IRON) return new TextureRegion(manager.get("iron.png", Texture.class));
        if(s == Symbol.DIAMOND) return new TextureRegion(manager.get("diamond.png", Texture.class));
        if(s == Symbol.SEVEN) return new TextureRegion(manager.get("seven.png", Texture.class));
        return baseMap.get(s);
    }

    public Animation<TextureAtlas.AtlasRegion> getBorderAnimation(Symbol s) {
        return borderMap.get(s);
    }

    public Texture getPokerChip() {
        return manager.get("base-poker-chip.png", Texture.class);
    }

    public Texture getSlotMachineBorder() {
        return manager.get("SlotMachineBorder.png", Texture.class);
    }

    public Texture getSlotMachineScreen() {
        return manager.get("SlotMachineScreen.png", Texture.class);
    }

    public Texture getButtonsLeftDisplay() {
        return manager.get("ButtonsLeftDisplay.png", Texture.class);
    }

    public Texture getPatternDisplay() {
        return manager.get("PatternDisplay.png", Texture.class);
    }

    public Texture getUncommonStick() {
        return manager.get("sticks/uncommon-stick.png", Texture.class);
    }

    public Texture getCommonStick() {
        return manager.get("sticks/common-stick.png", Texture.class);
    }

    public Texture getRareStick() {
        return manager.get("sticks/rare-stick.png", Texture.class);
    }

    public Texture getEpicStick() {
        return manager.get("sticks/epic-stick.png", Texture.class);
    }

    public Texture getLegendaryStick() {
        return manager.get("sticks/legendary-stick.png", Texture.class);
    }

    public Texture getScoreBorder() {
        return manager.get("ScoreBorder.png", Texture.class);
    }

    public Texture getButtonBoard() {
        return manager.get("buttons/button-board.png", Texture.class);
    }

    public Texture getApplyButton() {
        return manager.get("buttons/apply-button.png", Texture.class);
    }

    public Texture getApplyButtonPressed() {
        return manager.get("buttons/apply-button-pressed.png", Texture.class);
    }

    public Texture getSpinButton() {
        return manager.get("buttons/spin-button.png", Texture.class);
    }

    public Texture getSpinButtonPressed() {
        return manager.get("buttons/spin-button-pressed.png", Texture.class);
    }

    public Color colorBlue() {
        return new Color(0.1647f, 0.5412f, 0.7843f, 1f);
    }

    public Color colorRed() {
        return new Color(0.7922f, 0.3765f, 0.3333f, 1f);
    }

    public Color lightColor() {
        return new Color(1f, 0.996f, 0.8117f, 1f);
    }

    public BitmapFont getBigFont() {
        return bigFont;
    }

    public BitmapFont getSmallFont() {
        return smallFont;
    }

    public Texture unlitNumber() {
        return manager.get("digital-numbers/unlit.png", Texture.class);
    }

    public Texture getDigitalNumber(long number) {
        return manager.get("digital-numbers/" + number + ".png", Texture.class);
    }

}
