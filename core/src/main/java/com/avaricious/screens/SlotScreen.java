package com.avaricious.screens;

import box2dLight.RayHandler;
import com.avaricious.*;
import com.avaricious.components.*;
import com.avaricious.components.background.BackgroundLights;
import com.avaricious.components.background.WarpBackground;
import com.avaricious.components.buttons.Button;
import com.avaricious.components.buttons.DisablableButton;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.components.displays.TurnsLeftDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.progressbar.HealthBar;
import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.Symbol;
import com.avaricious.components.slot.pattern.SlotMatch;
import com.avaricious.upgrades.UpgradesManager;
import com.avaricious.upgrades.multAdditions.pattern.PatternMultAdditionUpgrade;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;
import com.crashinvaders.vfx.effects.util.MixEffect;

import java.util.List;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final VfxManager vfxManager;
    private final SlotMachine slotMachine;
    private final HealthBar healthBar;
    private final Shop shop;

    private final ScoreDisplay scoreDisplay;
    private final TurnsLeftDisplay turnsLeftDisplay;
    private final PatternDisplay patternDisplay;
    private final UpgradeSticks upgradeSticks;
    private final Button spinAgainButton;
    private final DisablableButton cashoutButton;
    private final Button shopButton;
    private final UpgradeBar upgradeBar;

    private final CameraShaker cameraShaker;
    private final World world;
    private final RayHandler rayHandler;
    private final WarpBackground background;
    private final BackgroundLights backgroundLights;

    private final InputHandler inputHandler;
    private final RoundsManager roundsManager;
    private final Vector2 mouse = new Vector2();
    private boolean leftClickWasPressed = false;

    public SlotScreen(Main app) {
        this.app = app;
        this.world = new World(new Vector2(0, 0), true);
//        RayHandler.setGammaCorrection(true);
//        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(world);

        background = new WarpBackground();
        backgroundLights = new BackgroundLights(rayHandler);
        healthBar = new HealthBar(100f);
        upgradeBar = new UpgradeBar(UpgradesManager.I().getUpgrades(),
            new Rectangle(3.5f, 0.5f, 142 / 115f, 190 / 115f),
            1.75f, true);

        scoreDisplay = new ScoreDisplay();
        turnsLeftDisplay = new TurnsLeftDisplay();
        patternDisplay = new PatternDisplay();
        upgradeSticks = new UpgradeSticks();
        shop = new Shop(() -> upgradeBar.loadUpgrades(UpgradesManager.I().getUpgrades()));
        spinAgainButton = new Button(this::onSpinButtonPressed,
            Assets.I().getSpinAgainButton(), Assets.I().getSpinAgainPressedButton(), Assets.I().getSpinAgainButtonHovered(),
            new Rectangle(12.5f, 2.6f, 79 / 35f, 25 / 35f), Input.Keys.SPACE);
        cashoutButton = new DisablableButton(this::onApplyButtonPressed,
            Assets.I().getCashoutButton(),
            Assets.I().getCashoutButtonPressed(),
            Assets.I().getCashoutButtonHovered(),
            Assets.I().getCashoutButtonDisabled(),
            new Rectangle(8f, 2.6f, 79 / 35f, 25 / 35f), Input.Keys.ENTER);
        shopButton = new Button(shop::show,
            Assets.I().getShopButton(), Assets.I().getShopButtonPressed(), Assets.I().getShopButtonHovered(),
            new Rectangle(12.5f, 0.5f, 79 / 35f, 25 / 35f), Input.Keys.ESCAPE);

        slotMachine = new SlotMachine(app.getViewport().getWorldWidth(), app.getViewport().getWorldHeight(), rayHandler);
        cameraShaker = new CameraShaker(app);

        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        vfxManager.addEffect(new OldTvEffect());
        vfxManager.addEffect(new MotionBlurEffect(Pixmap.Format.RGBA8888, MixEffect.Method.MAX, 0.5f));
//        vfxManager.addEffect(new CrtEffect());

        inputHandler = InputHandler.I();
        roundsManager = RoundsManager.I();
    }

    @Override
    public void show() {
        roundsManager.nextRound();
        scoreDisplay.resetScore();
        rayHandler.setAmbientLight(1f);

        //progressBar.damage(10f);
        slotMachine.getReels().get(slotMachine.getReels().size() -1).setOnSpinFinished(this::runResult);
        slotMachine.spin();
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = app.getBatch();
        handleInput(delta);
        app.getViewport().apply();

        rayHandler.updateAndRender();
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();
        cameraShaker.render(delta);

        background.render(batch, delta);
        Camera camera = app.getViewport().getCamera();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        healthBar.draw(batch);
        upgradeBar.draw(batch);
        spinAgainButton.draw(batch, delta);
        cashoutButton.draw(batch, delta);
        shopButton.draw(batch, delta);
        slotMachine.draw(app, delta);
        scoreDisplay.draw(batch, delta);
        patternDisplay.draw(batch, delta);
        shop.draw(batch, delta);
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

        spinAgainButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        cashoutButton.handleInput(mouse, leftClickPressed, leftClickWasPressed, patternDisplay.isEmpty());
        shopButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        upgradeBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        upgradeSticks.hoveringAt(mouse);

        leftClickWasPressed = leftClickPressed;
    }

    private void runResult() {

        List<SlotMatch> matches = slotMachine.findMatches();
        if(matches.isEmpty()) {
            int count = slotMachine.getSymbols().stream()
                .mapToInt(Symbol::baseValue)
                .sum();
            healthBar.damage(count);
            if(healthBar.getCurrentHealth() <= 0) {
                healthBar.setCurrentHealth(healthBar.getMaxHealth());
                patternDisplay.reset();
            }
            return;
        }

        float[] delayCounter = {0f};
        matches.forEach((slotMatch -> {
            slotMatch.slots().forEach(slot -> {
                Timer.schedule(TaskFactory.create(() -> {
                    slot.wobble();
                    slot.pulse();
                    patternDisplay.addPoints(slotMatch.symbol().baseValue());
                    PopupManager.I().spawnNumber(Assets.I().getDigitalNumber(slotMatch.symbol().baseValue()), Assets.I().colorBlue(),
                        slot.getPos().x + 1f, slot.getPos().y + 1f);
                }), delayCounter[0]);
                delayCounter[0] += 0.3f;
            });
            Timer.schedule(TaskFactory.create(() -> {
                    List<Slot> slots = slotMatch.slots();
                    slots.forEach(Slot::wobble);
                    slots.forEach(Slot::pulse);
                    patternDisplay.addMulti(slots.size());
                    Slot middleSlot = slots.get(slots.size() / 2 - (slots.size() % 2 == 0 ? 1 : 0));
                PopupManager.I().spawnNumber(Assets.I().getDigitalNumber(slots.size()), Assets.I().colorRed(),
                        middleSlot.getPos().x + 1f, middleSlot.getPos().y + 1f);
            }), delayCounter[0]);
            delayCounter[0] += 0.3f;

            UpgradesManager.I().getUpgrades()
                .stream().filter(PatternMultAdditionUpgrade.class::isInstance)
                .map(PatternMultAdditionUpgrade.class::cast)
                .filter(upgrade -> upgrade.condition(null, slotMatch.slots().size()))
                .forEach(upgrade -> {
                    Timer.schedule(TaskFactory.create(() -> {
                        int multi = upgrade.getMulti();
                        Slot cardSlot = upgradeBar.getSlotByUpgrade(upgrade);
                        cardSlot.pulse();
                        cardSlot.wobble();
                        patternDisplay.addMulti(multi);
                        PopupManager.I().spawnNumber(Assets.I().getDigitalNumber(multi), Assets.I().colorRed(),
                            upgradeBar.getRectangleByUpgrade(upgrade).x + 0.7f, 2.6f);
                    }), delayCounter[0]);
                    delayCounter[0] += 0.3f;
                });
        }));

        Timer.schedule(TaskFactory.create(() -> patternDisplay.addXMulti(1)), delayCounter[0]);
        delayCounter[0] += 0.5f;
    }

    private void onSpinButtonPressed() {
        slotMachine.spin();
        scoreDisplay.removeFromScore(50);

        backgroundLights.triggerLightShake(1f);
        cameraShaker.trigger(1f);
    }

    private void onApplyButtonPressed() {
        scoreDisplay.addToScore(Math.round(patternDisplay.getPoints() * patternDisplay.getMulti() * patternDisplay.getXMulti()));
        patternDisplay.reset();
        healthBar.setCurrentHealth(healthBar.getMaxHealth());

        backgroundLights.triggerLightShake(1f);
        cameraShaker.trigger(1f);
    }
}
