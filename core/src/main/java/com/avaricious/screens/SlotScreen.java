package com.avaricious.screens;

import com.avaricious.*;
import com.avaricious.components.*;
import com.avaricious.components.buttons.DisablableButton;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.components.displays.TurnsLeftDisplay;
import com.avaricious.components.popups.NumberPopup;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.progressbar.HealthBar;
import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.pattern.SlotMatch;
import com.avaricious.screens.mainscreen.BackgroundLayer;
import com.avaricious.screens.mainscreen.MainScreen;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.CreditSpawnChance;
import com.avaricious.stats.statupgrades.CritChance;
import com.avaricious.stats.statupgrades.DoubleHitChance;
import com.avaricious.upgrades.RetriggerUpgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.avaricious.upgrades.multAdditions.pattern.PatternMultAdditionUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.SymbolValueStackUpgrade;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;
import com.crashinvaders.vfx.effects.util.MixEffect;

import javax.swing.*;
import java.util.List;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final VfxManager vfxManager;
    private final SlotMachine slotMachine;
    private final HealthBar healthBar;
    private final Shop shop;
    private final StatUpgradeWindow statUpgradeWindow;

    private final BackgroundLayer backgroundLayer = new BackgroundLayer();

    private final ScoreDisplay scoreDisplay;
    private final PatternDisplay patternDisplay;
    private final UpgradeSticks upgradeSticks;
    private final DisablableButton spinAgainButton;
    private final DisablableButton cashoutButton;
    private final UpgradeBar upgradeBar;

    private final CameraShaker cameraShaker;

    private final RoundsManager roundsManager;
    private final Vector2 mouse = new Vector2();
    private boolean leftClickWasPressed = false;

    public SlotScreen(Main app) {
        this.app = app;
//        RayHandler.setGammaCorrection(true);
//        RayHandler.useDiffuseLight(true);

        healthBar = new HealthBar(100f);
        upgradeBar = new UpgradeBar(UpgradesManager.I().getUpgrades(),
            new Rectangle(3.5f, 0.5f, 142 / 115f, 190 / 115f),
            1.75f, true);

        scoreDisplay = new ScoreDisplay();
        patternDisplay = new PatternDisplay();
        upgradeSticks = new UpgradeSticks();
        shop = new Shop(() -> upgradeBar.loadUpgrades(UpgradesManager.I().getUpgrades()));
        statUpgradeWindow = new StatUpgradeWindow(() -> {
            if(scoreDisplay.targetScoreReached()) onTargetScoreReached();
        } );
        spinAgainButton = new DisablableButton(this::onSpinButtonPressed,
            Assets.I().getSpinAgainButton(),
            Assets.I().getSpinAgainPressedButton(),
            Assets.I().getSpinAgainButtonHovered(),
            Assets.I().getSpinAgainButtonDisabled(),
            new Rectangle(12f, 2.6f, 79 / 35f, 25 / 35f), Input.Keys.SPACE);
        cashoutButton = new DisablableButton(this::onApplyButtonPressed,
            Assets.I().getCashoutButton(),
            Assets.I().getCashoutButtonPressed(),
            Assets.I().getCashoutButtonHovered(),
            Assets.I().getCashoutButtonDisabled(),
            new Rectangle(8f, 2.6f, 79 / 35f, 25 / 35f), Input.Keys.ENTER);

        slotMachine = new SlotMachine(app.getViewport().getWorldWidth(), app.getViewport().getWorldHeight());
        cameraShaker = new CameraShaker(app);

        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        vfxManager.addEffect(new OldTvEffect());
        vfxManager.addEffect(new MotionBlurEffect(Pixmap.Format.RGBA8888, MixEffect.Method.MAX, 0.5f));
//        vfxManager.addEffect(new CrtEffect());

        roundsManager = RoundsManager.I();
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
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = app.getBatch();
        handleInput(delta);
        app.getViewport().apply();

        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();
        cameraShaker.render(delta);

        backgroundLayer.render(batch, delta);
        Camera camera = app.getViewport().getCamera();
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

        batch.begin();
        healthBar.draw(batch);
        upgradeBar.draw(batch);
        spinAgainButton.draw(batch, delta);
        cashoutButton.draw(batch, delta);
        slotMachine.draw(app, delta);
        scoreDisplay.draw(batch, delta);
        patternDisplay.draw(batch, delta);
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

        app.getUiViewport().apply();
        batch.setProjectionMatrix(app.getUiViewport().getCamera().combined);
    }


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
        upgradeBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        upgradeSticks.hoveringAt(mouse);

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
            return;
        }

        healthBar.heal(20);

        TaskScheduler scheduler = new TaskScheduler(0.3f);
        matches.forEach((slotMatch -> {
            List<Slot> slots = slotMatch.slots();
            Slot middleSlot = slots.get(slots.size() / 2 - (slots.size() % 2 == 0 ? 1 : 0));

            triggerSeparateSlots(slotMatch, scheduler);
            if(PlayerStats.I().rollChance(DoubleHitChance.class)) {
                scheduler.schedule(() -> PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(DoubleHitChance.class).getTexture(),
                        middleSlot.getPos().x + 1f, middleSlot.getPos().y + 1f));
                triggerSeparateSlots(slotMatch, scheduler);
            }

            scheduler.schedule(() -> {
                slots.forEach(Slot::wobble);
                slots.forEach(Slot::pulse);
                patternDisplay.addMulti(slots.size());
                PopupManager.I().spawnNumber(slots.size(), Assets.I().colorRed(),
                    middleSlot.getPos().x + 1f, middleSlot.getPos().y + 1f);
            });
            UpgradesManager.I().getUpgradesOfClass(PatternMultAdditionUpgrade.class)
                .filter(upgrade -> upgrade.condition(null, slotMatch.slots().size()))
                .forEach(upgrade -> {
                    scheduler.schedule(() -> {
                        int multi = upgrade.getMulti();
                        Slot cardSlot = upgradeBar.getSlotByUpgrade(upgrade);
                        cardSlot.pulse();
                        cardSlot.wobble();
                        patternDisplay.addMulti(multi);
                        PopupManager.I().spawnNumber(multi, Assets.I().colorRed(),
                            upgradeBar.getRectangleByUpgrade(upgrade).x + 0.7f, 2.6f);
                    });
                });
        }));
        scheduler.schedule(() -> patternDisplay.addStreak(1));
        scheduler.schedule(() -> {
            spinAgainButton.setDisabled(false);
            cashoutButton.setDisabled(false);
        });
        scheduler.runTasks();
    }

    private void triggerSeparateSlots(SlotMatch slotMatch, TaskScheduler scheduler) {
        slotMatch.slots().forEach(slot -> {
            scheduler.schedule(() -> {
                slot.wobble();
                slot.pulse();
                if(PlayerStats.I().rollChance(CritChance.class)) {
                    PopupManager.I().spawnNumber(slotMatch.symbol().baseValue() * 3, Assets.I().colorBlue(),
                        slot.getPos().x + 1f, slot.getPos().y + 1f);
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CritChance.class).getTexture(),
                        slot.getPos().x + 2f, slot.getPos().y + 1f);
                    patternDisplay.addPoints(slotMatch.symbol().baseValue() * 3);
                } else {
                    PopupManager.I().spawnNumber(slotMatch.symbol().baseValue(), Assets.I().colorBlue(),
                        slot.getPos().x + 1f, slot.getPos().y + 1f);
                    patternDisplay.addPoints(slotMatch.symbol().baseValue());
                }
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
                    Slot upgradeSlot = upgradeBar.getSlotByUpgrade(upgrade);
                    scheduler.schedule(() -> {
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
        statUpgradeWindow.show();
        scoreDisplay.addToScore(Math.round(patternDisplay.getPoints() * patternDisplay.getMulti() * patternDisplay.getXMulti()));
        patternDisplay.reset();
        cashoutButton.setDisabled(true);
    }

    private void onTargetScoreReached() {
        RoundsManager.I().nextRound();
        patternDisplay.reset();
        scoreDisplay.nextRound();
        healthBar.fullHeal();
        shop.show();
    }
}
