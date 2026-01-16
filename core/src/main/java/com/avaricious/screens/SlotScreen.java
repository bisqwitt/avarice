package com.avaricious.screens;

import com.avaricious.*;
import com.avaricious.audio.AudioManager;
import com.avaricious.components.*;
import com.avaricious.components.buttons.DisablableButton;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.progressbar.HealthBar;
import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.pattern.SlotMatch;
import com.avaricious.screens.mainscreen.BackgroundLayer;
import com.avaricious.screens.mainscreen.MainScreen;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.CreditSpawnChance;
import com.avaricious.stats.statupgrades.CriticalHitChance;
import com.avaricious.stats.statupgrades.DoubleHitChance;
import com.avaricious.upgrades.UpgradesManager;
import com.avaricious.upgrades.bars.JokerDeck;
import com.avaricious.upgrades.multAdditions.pattern.PatternMultAdditionUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.SymbolValueStackUpgrade;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;

import java.util.List;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final SlotMachine slotMachine;
    private final Texture slotMachineBox = Assets.I().getSlotMachineBox();
    private final HealthBar healthBar = new HealthBar(100f);
    private final XpBar xpBar;

    private final ScoreDisplay scoreDisplay = new ScoreDisplay();
    private final PatternDisplay patternDisplay = new PatternDisplay();

    private final StatUpgradeWindow statUpgradeWindow = new StatUpgradeWindow(() -> {
        if(scoreDisplay.targetScoreReached()) onTargetScoreReached();
    } );

    private final BackgroundLayer backgroundLayer = new BackgroundLayer();

    private final DisablableButton spinAgainButton = new DisablableButton(this::onSpinButtonPressed,
        Assets.I().getSpinAgainButton(),
        Assets.I().getSpinAgainPressedButton(),
        Assets.I().getSpinAgainButtonHovered(),
        Assets.I().getSpinAgainButtonDisabled(),
        new Rectangle(8.7f, 2.4f, 79 / 35f, 25 / 35f), Input.Keys.SPACE);

    private final DisablableButton cashoutButton = new DisablableButton(this::onApplyButtonPressed,
        Assets.I().getCashoutButton(),
        Assets.I().getCashoutButtonPressed(),
        Assets.I().getCashoutButtonHovered(),
        Assets.I().getCashoutButtonDisabled(),
        new Rectangle(4.7f, 2.4f, 79 / 35f, 25 / 35f), Input.Keys.ENTER);

//    private final Button shopButton = new Button(this::)

    private final JokerDeck jokerDeck = new JokerDeck(
        new Rectangle(13.25f, 1f, 142 / 95f, 190 / 95f)
    );

    private final CreditScore creditScore = new CreditScore(0,
        new Rectangle(1f, 1f, 0.32f * 1.5f, 0.56f * 1.5f), 0.35f * 1.5f);

    private final Shop shop = new Shop();

    private final VfxManager vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
    private final VfxManager bloomFxManager = new VfxManager(Pixmap.Format.RGBA8888);
    private final FrameBuffer bloomLayer = new FrameBuffer(
        Pixmap.Format.RGBA8888,
        16,
        9,
        false
    );


    private final CameraShaker cameraShaker;

    private final RoundsManager roundsManager = RoundsManager.I();
    private final Vector2 mouse = new Vector2();
    private boolean leftClickWasPressed = false;

    public SlotScreen(Main app) {
        this.app = app;
//        RayHandler.setGammaCorrection(true);
//        RayHandler.useDiffuseLight(true);

        slotMachine = new SlotMachine(app.getViewport().getWorldWidth(), app.getViewport().getWorldHeight());
        xpBar = new XpBar(statUpgradeWindow::show);

        cameraShaker = new CameraShaker(app);

        vfxManager.addEffect(new OldTvEffect());
        BloomEffect bloom = new BloomEffect();
        bloom.setBaseIntensity(1f);
        bloom.setBloomIntensity(10f);
        bloom.setThreshold(0.6f);
        bloom.setBlurAmount(2f);
        bloom.setBlurPasses(3);
        bloomFxManager.addEffect(bloom);
    }

    @Override
    public void show() {
        roundsManager.nextRound();
        scoreDisplay.resetScore();
        cashoutButton.setDisabled(true);

        backgroundLayer.init();

        healthBar.setCurrentHealth(healthBar.getMaxHealth());
        slotMachine.getReels().get(slotMachine.getReels().size() -1).setOnSpinFinished(this::runResult);

        shop.show();
//        statUpgradeWindow.show();
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = app.getBatch();
        handleInput(delta);
        app.getViewport().apply();

        vfxManager.cleanUpBuffers();
        cameraShaker.render(delta);

        vfxManager.beginInputCapture();
//        backgroundLayer.render(batch, delta);
        Camera camera = app.getViewport().getCamera();
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.2078f, 0.39607f, 0.30196f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        TextureEcho.draw(batch, delta);
        ParticleManager.I().draw(batch, delta);
//        healthBar.draw(batch);
        //upgradeBar.draw(batch);
//        spinAgainButton.draw(batch, delta);
//        cashoutButton.draw(batch, delta);
//        batch.draw(slotMachineBox, 6.75f, 3.0f, 175f / 20.75f, 118 / 20.75f);
        slotMachine.draw(app, delta);
        scoreDisplay.draw(batch, delta);
        patternDisplay.draw(batch, delta);
        creditScore.draw(batch, delta);
        jokerDeck.draw(batch, delta);
        xpBar.draw(batch);

        TextureGlow.draw(batch, delta, "number");

        shop.draw(batch, delta);
        statUpgradeWindow.draw(batch, delta);
        PopupManager.I().draw(batch, delta);
        batch.end();

        vfxManager.endInputCapture();
        vfxManager.applyEffects();
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
        vfxManager.renderToScreen();
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

//        renderBloomedLayer(batch, delta);
    }

//    private void renderBloomedLayer(SpriteBatch batch, float delta) {
//
//        // 1) Capture ONLY symbol echos into bloom manager (world coordinates)
//        bloomFxManager.cleanUpBuffers();
//        bloomFxManager.beginInputCapture();
//
//        batch.begin();
//        symbolEchos.forEach(echo -> echo.draw(batch, delta));
//        batch.end();
//
//        bloomFxManager.endInputCapture();
////        bloomFxManager.applyEffects();
//
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE); // additive glow
//
//        Texture bloomTex = bloomFxManager.getResultBuffer().getTexture();
//
//        batch.begin();
//        batch.draw(
//            bloomTex,
//            0, 0,
//            16, 9,
//            0, 0, 1, 1 // typical FBO flip; if it appears upside down, use 0,0,1,1
//        );
//        batch.end();
//
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//    }



    private void handleInput(float delta) {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        app.getViewport().unproject(mouse);
        boolean leftClickPressed = Gdx.input.isButtonPressed(0);

        if(shop.isShowing()) {
            shop.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
            leftClickWasPressed = leftClickPressed;
            return;
        }
        statUpgradeWindow.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);

        backgroundLayer.handleInput();

        spinAgainButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        cashoutButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        jokerDeck.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);

        leftClickWasPressed = leftClickPressed;
    }

    private void runResult() {
        List<SlotMatch> matches = slotMachine.findMatches();
        if(matches.isEmpty()) {
            healthBar.damage(20);
            patternDisplay.reset();
            if(healthBar.getCurrentHealth() <= 0) {
                ScreenManager.I().setScreen(MainScreen.class);
            }
            spinAgainButton.setDisabled(false);

            if(DevTools.autoSpin) onSpinButtonPressed();
            return;
        }

        healthBar.heal(20);
        TaskScheduler scheduler = new TaskScheduler(0.325f);
        scheduler.schedule(() -> slotMachine.setRunningResults(true), 0f);

        matches.forEach((slotMatch -> {
            List<Slot> slots = slotMatch.slots();
            Slot middleSlot = slots.get(slots.size() / 2 - (slots.size() % 2 == 0 ? 1 : 0));

            scheduler.scheduleImmediate(() -> slots.forEach(slot -> {
                slot.targetScale = 1.25f;
                slot.setInPatternHit(true);
            }));

            triggerSeparateSlots(slotMatch, scheduler);
            if(PlayerStats.I().rollChance(DoubleHitChance.class)) {
                scheduler.schedule(() -> PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(DoubleHitChance.class).getTexture(),
                        middleSlot.getPos().x + 1f, middleSlot.getPos().y + 1f));
                triggerSeparateSlots(slotMatch, scheduler);
            }

            scheduler.schedule(() -> {
                slots.forEach(slot -> {
                    slot.wobble();
                    slot.pulse();

                    EffectManager.create(Assets.I().getBase(slotMatch.symbol()),
                        new Rectangle(slot.getPos().x, slot.getPos().y, SlotMachine.CELL_W, SlotMachine.CELL_H),
                        "slot", new Color(1f, 1f, 1f, 1f));
                });

                boolean criticalHit = PlayerStats.I().rollChance(CriticalHitChance.class);
                int mult = criticalHit ? slots.size() * 2 : slots.size();

                PopupManager.I().spawnNumber(mult, Assets.I().colorRed(),
                    middleSlot.getPos().x + ((slots.size() % 2 == 0) ? 2f : 1.5f), middleSlot.getPos().y + 1f);
                if(criticalHit) PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CriticalHitChance.class).getTexture(),
                    middleSlot.getPos().x + 2.5f, middleSlot.getPos().y + 1f);
                patternDisplay.addMulti(mult);

                AudioManager.I().playHit(EffectManager.streak);
            });

            UpgradesManager.I().getUpgradesOfClass(PatternMultAdditionUpgrade.class)
                .filter(upgrade -> upgrade.condition(null, slotMatch.slots().size()))
                .forEach(upgrade -> scheduler.schedule(() -> {
                        int multi = upgrade.getMulti();
                        Slot cardSlot = jokerDeck.getSlotByUpgrade(upgrade);
                        cardSlot.pulse();
                        cardSlot.wobble();
                        patternDisplay.addMulti(multi);
                        PopupManager.I().spawnNumber(multi, Assets.I().colorRed(),
                            jokerDeck.getBoundsByUpgrade(upgrade).x + 1.5f, 2.6f);
                    }));

            scheduler.schedule(() -> {
                EffectManager.increaseStreak();
                slots.forEach(slot -> {
                    slot.targetScale = 1f;
                    slot.setInPatternHit(false);
                });
            });
        }));

        scheduler.schedule(() -> {
            patternDisplay.addStreak(1);
            spinAgainButton.setDisabled(false);
            cashoutButton.setDisabled(false);
            slotMachine.setRunningResults(false);
            EffectManager.endStreak();
        });

        if(DevTools.autoSpin) {
            scheduler.schedule(this::onApplyButtonPressed);
            scheduler.schedule(this::onSpinButtonPressed);
        }
        scheduler.runTasks();
    }

    private void triggerSeparateSlots(SlotMatch slotMatch, TaskScheduler scheduler) {
        slotMatch.slots().forEach(slot -> {
            scheduler.schedule(() -> {
                slot.wobble();
                slot.pulse();

                boolean criticalHit = PlayerStats.I().rollChance(CriticalHitChance.class);
                int points = criticalHit ? slotMatch.symbol().baseValue() * 2 : slotMatch.symbol().baseValue();

                PopupManager.I().spawnNumber(points, Assets.I().colorBlue(),
                    slot.getPos().x + 1.5f, slot.getPos().y + 1f);
                if(criticalHit) PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CriticalHitChance.class).getTexture(),
                    slot.getPos().x + 2.5f, slot.getPos().y + 1f);
                patternDisplay.addPoints(points);

                EffectManager.create(Assets.I().getBase(slotMatch.symbol()),
                    new Rectangle(slot.getPos().x, slot.getPos().y, SlotMachine.CELL_W, SlotMachine.CELL_H),
                    "slot", new Color(1f, 1f, 1f, 1f));

                AudioManager.I().playHit(EffectManager.streak);

                xpBar.addXp(points);
            });

            if(PlayerStats.I().rollChance(CreditSpawnChance.class)) {
                scheduler.schedule(() -> {
                    float x = slot.getPos().x + 1f;
                    float y = slot.getPos().y + 1f;
                    PopupManager.I().spawnNumber(1, Assets.I().colorYellow(), x, y);
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CreditSpawnChance.class).getTexture(), x + 1f, y);
                    CreditManager.I().gain(1);
                });
            }

            UpgradesManager.I().getUpgradesOfClass(SymbolValueStackUpgrade.class)
                .filter(upgrade -> upgrade.getSymbol() == slotMatch.symbol())
                .forEach(upgrade -> {
                    Slot upgradeSlot = jokerDeck.getSlotByUpgrade(upgrade);
                    scheduler.scheduleImmediate(() -> {
                        upgradeSlot.wobble();
                        upgradeSlot.pulse();
                        PopupManager.I().spawnNumber(1, Assets.I().colorGreen(),
                            upgradeSlot.getPos().x, upgradeSlot.getPos().y + 1.5f);
                    });
                    if(upgrade.addStacks(1)) {
                        scheduler.schedule(() -> {
                            upgradeSlot.wobble();
                            upgradeSlot.pulse();
                            PopupManager.I().spawnNumber(1, Assets.I().colorBlue(),
                                upgradeSlot.getPos().x, upgradeSlot.getPos().y + 1.5f);
                        });
                    }
                });
        });
    }

    private void onSpinButtonPressed() {
        slotMachine.spin();
        spinAgainButton.setDisabled(true);
        cashoutButton.setDisabled(true);
    }

    private void onApplyButtonPressed() {
//        statUpgradeWindow.show();
        scoreDisplay.addToScore(Math.round(patternDisplay.getPoints() * patternDisplay.getMulti() * patternDisplay.getXMulti()));
        patternDisplay.spawnEcho();
        patternDisplay.reset();
        cashoutButton.setDisabled(true);
    }

    private void onTargetScoreReached() {
//        RoundsManager.I().nextRound();
//        CreditManager.I().roundEnd();
//        patternDisplay.reset();
//        scoreDisplay.nextRound();
//        healthBar.fullHeal();
//        shop.show();
    }
}
