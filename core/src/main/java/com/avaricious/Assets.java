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
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import org.w3c.dom.Text;

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
        manager.load("base-poker-chip-shadow.png", Texture.class);
        manager.load("PatternDisplay.png", Texture.class);
        manager.load("cable.png", Texture.class);
        manager.load("CoinSlot.png", Texture.class);
        manager.load("white.png", Texture.class);
        manager.load("retrigger.png", Texture.class);
        manager.load("SlotMachineShadow.png", Texture.class);
        manager.load("Joker.png", Texture.class);
        manager.load("JokerShadow.png", Texture.class);
        manager.load("JokerCardPrice.png", Texture.class);
        manager.load("TooltipBox.png", Texture.class);
        manager.load("TooltipBox-shadow.png", Texture.class);
        manager.load("ShopWindow.png", Texture.class);
        manager.load("ShopWindowShadow.png", Texture.class);
        manager.load("UpgradeWindow.png", Texture.class);
        manager.load("UpgradeWindow-shadow.png", Texture.class);
        manager.load("quest-scroll.png", Texture.class);
        manager.load("slotBox.png", Texture.class);
        manager.load("slotBox-shadow.png", Texture.class);
        manager.load("slotMachineBox.png", Texture.class);
        manager.load("metal-texture.png", Texture.class);
        manager.load("ScoreDisplayBackground.png", Texture.class);
        manager.load("dark-green.png", Texture.class);
        manager.load("black-green-pixel.png", Texture.class);
        manager.load("xp-pixel.png", Texture.class);

        manager.load("buttons/spinagain/spin-again.png", Texture.class);
        manager.load("buttons/spinagain/spin-again-pressed.png", Texture.class);
        manager.load("buttons/spinagain/spin-again-hovered.png", Texture.class);
        manager.load("buttons/spinagain/spin-again-disabled.png", Texture.class);
        manager.load("buttons/cashout/cashout.png", Texture.class);
        manager.load("buttons/cashout/cashout-pressed.png", Texture.class);
        manager.load("buttons/cashout/cashout-hovered.png", Texture.class);
        manager.load("buttons/cashout/cashout-disabled.png", Texture.class);
        manager.load("buttons/nextround/next-round.png", Texture.class);
        manager.load("buttons/nextround/next-round-pressed.png", Texture.class);
        manager.load("buttons/nextround/next-round-hovered.png", Texture.class);
        manager.load("buttons/shop/shop.png", Texture.class);
        manager.load("buttons/shop/shop-hovered.png", Texture.class);
        manager.load("buttons/shop/shop-pressed.png", Texture.class);
        manager.load("buttons/return/return.png", Texture.class);
        manager.load("buttons/return/return-hovered.png", Texture.class);
        manager.load("buttons/return/return-pressed.png", Texture.class);
        manager.load("buttons/reroll/reroll.png", Texture.class);
        manager.load("buttons/reroll/reroll-hovered.png", Texture.class);
        manager.load("buttons/reroll/reroll-pressed.png", Texture.class);
        manager.load("buttons/enter/enter.png", Texture.class);
        manager.load("buttons/enter/enter-hovered.png", Texture.class);
        manager.load("buttons/enter/enter-pressed.png", Texture.class);

        manager.load("progressbar/ProgressBarBorder.png", Texture.class);
        manager.load("progressbar/ProgressBarLit.png", Texture.class);
        manager.load("progressbar/ProgressBarUnlit.png", Texture.class);

        manager.load("digital-numbers/plus.png", Texture.class);
        manager.load("digital-numbers/plus-shadow.png", Texture.class);
        manager.load("digital-numbers/mult.png", Texture.class);
        manager.load("digital-numbers/mult-shadow.png", Texture.class);
        manager.load("digital-numbers/unlit.png", Texture.class);
        manager.load("digital-numbers/fabled/percentage.png", Texture.class);
        manager.load("digital-numbers/fabled/dollar.png", Texture.class);
        manager.load("digital-numbers/fabled/0.png", Texture.class);
        manager.load("digital-numbers/fabled/1.png", Texture.class);
        manager.load("digital-numbers/fabled/2.png", Texture.class);
        manager.load("digital-numbers/fabled/3.png", Texture.class);
        manager.load("digital-numbers/fabled/4.png", Texture.class);
        manager.load("digital-numbers/fabled/5.png", Texture.class);
        manager.load("digital-numbers/fabled/6.png", Texture.class);
        manager.load("digital-numbers/fabled/7.png", Texture.class);
        manager.load("digital-numbers/fabled/8.png", Texture.class);
        manager.load("digital-numbers/fabled/9.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/percentage.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/dollar.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/0.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/1.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/2.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/3.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/4.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/5.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/6.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/7.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/8.png", Texture.class);
        manager.load("digital-numbers/fabled/shadows/9.png", Texture.class);

        manager.load("sticks/uncommon-stick.png", Texture.class);
        manager.load("sticks/common-stick.png", Texture.class);
        manager.load("sticks/rare-stick.png", Texture.class);
        manager.load("sticks/epic-stick.png", Texture.class);
        manager.load("sticks/legendary-stick.png", Texture.class);

        manager.load("symbols/64/lemon.png", Texture.class);
        manager.load("symbols/64/cherry.png", Texture.class);
        manager.load("symbols/64/clover.png", Texture.class);
        manager.load("symbols/64/bell.png", Texture.class);
        manager.load("symbols/iron.png", Texture.class);
        manager.load("symbols/diamond.png", Texture.class);
        manager.load("symbols/seven.png", Texture.class);

        manager.load("stats/coin.png", Texture.class);
        manager.load("stats/critical hit.png", Texture.class);
        manager.load("stats/evade.png", Texture.class);
        manager.load("stats/luck.png", Texture.class);
        manager.load("stats/multi.png", Texture.class);
        manager.load("stats/Retrigger.png", Texture.class);
        manager.load("stats/coin-shadow.png", Texture.class);
        manager.load("stats/critical hit-shadow.png", Texture.class);
        manager.load("stats/evade-shadow.png", Texture.class);
        manager.load("stats/luck-shadow.png", Texture.class);
        manager.load("stats/multi-shadow.png", Texture.class);
        manager.load("stats/Retrigger-shadow.png", Texture.class);

        atlas = new TextureAtlas(Gdx.files.internal("symbols.atlas"));
        atlas.getTextures().forEach(texture -> texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest));

        Arrays.asList(Symbol.values()).forEach(symbol -> {
            String name = symbol.name().toLowerCase() + "/" + symbol.name().toLowerCase();
            baseMap.put(symbol, atlas.findRegion(name));

            manager.load("symbolShadows/" + symbol.name().toLowerCase() + "-shadow.png", Texture.class);

            Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(name + "_border");
            Animation<TextureAtlas.AtlasRegion> anim = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
            borderMap.put(symbol, anim);
        });



        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/m6x11plus.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 36;
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
//        if(s == Symbol.LEMON) return new TextureRegion(manager.get("symbols/64/lemon.png", Texture.class));
//        if(s == Symbol.CHERRY) return new TextureRegion(manager.get("symbols/64/cherry.png", Texture.class));
//        if(s == Symbol.CLOVER) return new TextureRegion(manager.get("symbols/64/clover.png", Texture.class));
//        if(s == Symbol.BELL) return new TextureRegion(manager.get("symbols/64/bell.png", Texture.class));
//        if(s == Symbol.IRON) return new TextureRegion(manager.get("iron.png", Texture.class));
//        if(s == Symbol.DIAMOND) return new TextureRegion(manager.get("diamond.png", Texture.class));
//        if(s == Symbol.SEVEN) return new TextureRegion(manager.get("seven.png", Texture.class));
        return baseMap.get(s);
    }

    public TextureRegion getSymbolShadow(Symbol s) {
        return new TextureRegion(manager.get("symbolShadows/" + s.name().toLowerCase() + "-shadow.png", Texture.class));
    }

    public Texture getXpPixel() {
        return manager.get("xp-pixel.png", Texture.class);
    }

    public Texture getScoreDisplayBackground() {
        return manager.get("ScoreDisplayBackground.png", Texture.class);
    }

    public Animation<TextureAtlas.AtlasRegion> getBorderAnimation(Symbol s) {
        return borderMap.get(s);
    }

    public Texture getMetalTexture() {
        return manager.get("metal-texture.png", Texture.class);
    }

    public Texture getWhiteTexture() {
        return manager.get("white.png", Texture.class);
    }

    public Texture getPokerChip() {
        return manager.get("base-poker-chip.png", Texture.class);
    }

    public Texture getPokerChipShadow() {
        return manager.get("base-poker-chip-shadow.png", Texture.class);
    }

    public Texture getSlotMachineShadow() {
        return manager.get("SlotMachineShadow.png", Texture.class);
    }

    public Texture getJokerCard() {
        return manager.get("Joker.png", Texture.class);
    }

    public Texture getJokerCardShadow() {
        return manager.get("JokerShadow.png", Texture.class);
    }

    public Texture getJokerCardPriceBox() {
        return manager.get("JokerCardPrice.png", Texture.class);
    }

    public Texture getProgressBarBorder() {
        return manager.get("progressbar/ProgressBarBorder.png", Texture.class);
    }

    public Texture getDarkGreenTexture() {
        return manager.get("dark-green.png", Texture.class);
    }

    public Texture getTooltipBox() {
        return manager.get("TooltipBox.png", Texture.class);
    }

    public Texture getTooltipBoxShadow() {
        return manager.get("TooltipBox-shadow.png", Texture.class);
    }

    public Texture getProgressLit() {
        return manager.get("progressbar/ProgressBarLit.png", Texture.class);
    }

    public Texture getProgressUnlit() {
        return manager.get("progressbar/ProgressBarUnlit.png", Texture.class);
    }

    public Texture getQuestScroll() {
        return manager.get("quest-scroll.png", Texture.class);
    }

    public Texture getShopWindow() {
        return manager.get("ShopWindow.png", Texture.class);
    }

    public Texture getShopWindowShadow() {
        return manager.get("ShopWindowShadow.png", Texture.class);
    }

    public Texture getStatUpgradeWindow() {
        return manager.get("UpgradeWindow.png", Texture.class);
    }

    public Texture getStatUpgradeWindowShadow() {
        return manager.get("UpgradeWindow-shadow.png", Texture.class);
    }

    public Texture getCoinSlot() {
        return manager.get("CoinSlot.png", Texture.class);
    }

    public Texture getPlusSymbol() {
        return manager.get("digital-numbers/plus.png", Texture.class);
    }

    public Texture getPlusSymbolShadow() {
        return manager.get("digital-numbers/plus-shadow.png", Texture.class);
    }

    public Texture getPercentageSymbol() {
        return manager.get("digital-numbers/fabled/percentage.png", Texture.class);
    }

    public Texture getPercentageSymbolShadow() {
        return manager.get("digital-numbers/fabled/shadows/percentage.png", Texture.class);
    }

    public Texture getDollarSymbol() {
        return manager.get("digital-numbers/fabled/dollar.png", Texture.class);
    }

    public Texture getDollarSymbolShadow() {
        return manager.get("digital-numbers/fabled/shadows/dollar.png", Texture.class);
    }

    public Texture getRetriggerSymbol() {
        return manager.get("retrigger.png", Texture.class);
    }

    public Texture getSpinAgainButtonHovered() {
        return manager.get("buttons/spinagain/spin-again-hovered.png", Texture.class);
    }

    public Texture getSpinAgainButtonDisabled() {
        return manager.get("buttons/spinagain/spin-again-disabled.png", Texture.class);
    }

    public Texture getCashoutButton() {
        return manager.get("buttons/cashout/cashout.png", Texture.class);
    }

    public Texture getCashoutButtonPressed() {
        return manager.get("buttons/cashout/cashout-pressed.png", Texture.class);
    }

    public Texture getCashoutButtonHovered() {
        return manager.get("buttons/cashout/cashout-hovered.png", Texture.class);
    }

    public Texture getNextRoundButton() {
        return manager.get("buttons/nextround/next-round.png", Texture.class);
    }

    public Texture getNextRoundButtonHovered() {
        return manager.get("buttons/nextround/next-round-hovered.png", Texture.class);
    }

    public Texture getNextRoundButtonPressed() {
        return manager.get("buttons/nextround/next-round-pressed.png", Texture.class);
    }

    public Texture getCashoutButtonDisabled() {
        return manager.get("buttons/cashout/cashout-disabled.png", Texture.class);
    }

    public Texture getEnterButton() {
        return manager.get("buttons/enter/enter.png", Texture.class);
    }

    public Texture getEnterButtonHovered() {
        return manager.get("buttons/enter/enter-hovered.png", Texture.class);
    }

    public Texture getEnterButtonPressed() {
        return manager.get("buttons/enter/enter-pressed.png", Texture.class);
    }

    public Texture getRerollButton() {
        return manager.get("buttons/reroll/reroll.png", Texture.class);
    }

    public Texture getRerollButtonHovered() {
        return manager.get("buttons/reroll/reroll-hovered.png", Texture.class);
    }

    public Texture getRerollButtonPressed() {
        return manager.get("buttons/reroll/reroll-pressed.png", Texture.class);
    }

    public Texture getShopButton() {
        return manager.get("buttons/shop/shop.png", Texture.class);
    }

    public Texture getShopButtonHovered() {
        return manager.get("buttons/shop/shop-hovered.png", Texture.class);
    }

    public Texture getShopButtonPressed() {
        return manager.get("buttons/shop/shop-pressed.png", Texture.class);
    }

    public Texture getReturnButton() {
        return manager.get("buttons/return/return.png", Texture.class);
    }

    public Texture getReturnButtonHovered() {
        return manager.get("buttons/return/return-hovered.png", Texture.class);
    }

    public Texture getReturnButtonPressed() {
        return manager.get("buttons/return/return-pressed.png", Texture.class);
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

    public Texture getCable() {
        return manager.get("cable.png", Texture.class);
    }

    public Texture getSlotBox() {
        return manager.get("slotBox.png", Texture.class);
    }

    public Texture getSlotBoxShadow() {
        return manager.get("slotBox-shadow.png", Texture.class);
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

    public Texture getSpinAgainButton() {
        return manager.get("buttons/spinagain/spin-again.png", Texture.class);
    }

    public Texture getCoinStat() {
        return manager.get("stats/coin.png", Texture.class);
    }

    public Texture getCriticalHitStat() {
        return manager.get("stats/critical hit.png", Texture.class);
    }

    public Texture getEvadeStat() {
        return manager.get("stats/evade.png", Texture.class);
    }

    public Texture getLuckStat() {
        return manager.get("stats/luck.png", Texture.class);
    }

    public Texture getMultiStat() {
        return manager.get("stats/multi.png", Texture.class);
    }

    public Texture getRetriggerStat() {
        return manager.get("stats/Retrigger.png", Texture.class);
    }

    public Texture getCoinStatShadow() {
        return manager.get("stats/coin-shadow.png", Texture.class);
    }

    public Texture getCriticalHitStatShadow() {
        return manager.get("stats/critical hit-shadow.png", Texture.class);
    }

    public Texture getEvadeStatShadow() {
        return manager.get("stats/evade-shadow.png", Texture.class);
    }

    public Texture getLuckStatShadow() {
        return manager.get("stats/luck-shadow.png", Texture.class);
    }

    public Texture getMultiStatShadow() {
        return manager.get("stats/multi-shadow.png", Texture.class);
    }

    public Texture getRetriggerShadow() {
        return manager.get("stats/Retrigger-shadow.png", Texture.class);
    }

    public Texture getBlackGreenTexture() {
        return manager.get("black-green-pixel.png", Texture.class);
    }

    public Texture getSpinAgainPressedButton() {
        return manager.get("buttons/spinagain/spin-again-pressed.png", Texture.class);
    }
    public Color colorBlue() {
        return new Color(0.1647f, 0.5412f, 0.7843f, 1f);
    }

    public String blueText(String txt) {
        return "[#2A8AC8]" + txt + "[]";
    }

    public Color colorRed() {
        return new Color(0.7922f, 0.3765f, 0.3333f, 1f);
    }

    public String redText(String txt) {
        return "[#CA6055]" + txt + "[]";
    }

    public Color colorGreen() {
        return new Color(0.2980f, 0.7098f, 0.4470f, 1f);
    }

    public String greenText(String txt) {
        return "[#4CB572]" + txt + "[]";
    }

    public Color colorYellow() {
        return new Color(218f / 255f, 172f / 255f, 83f / 255f, 1f);
    }

    public Color lightColor() {
        return new Color(0.992156f, 0.992156f, 0.992156f, 1f);
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

    public Texture mult() {
        return manager.get("digital-numbers/mult.png", Texture.class);
    }

    public Texture multShadow() {
        return manager.get("digital-numbers/mult-shadow.png", Texture.class);
    }

    public Texture getDigitalNumber(long number) {
        return manager.get("digital-numbers/fabled/" + number + ".png", Texture.class);
    }

    public Texture getDigitalNumberShadow(long number) {
        return manager.get("digital-numbers/fabled/shadows/" + number + ".png", Texture.class);
    }

    public Texture getSlotMachineBox() {
        return manager.get("slotMachineBox.png", Texture.class);
    }

}
