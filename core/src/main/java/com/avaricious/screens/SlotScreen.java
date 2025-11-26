package com.avaricious.screens;

import box2dLight.RayHandler;
import com.avaricious.*;
import com.avaricious.components.*;
import com.avaricious.components.background.BackgroundLights;
import com.avaricious.components.background.WarpBackground;
import com.avaricious.components.buttons.SpinAgainButton;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.components.displays.TurnsLeftDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.progressbar.HealthBar;
import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.pattern.SlotMatch;
import com.avaricious.upgrades.UpgradesManager;
import com.avaricious.upgrades.multAdditions.pattern.PatternMultAdditionUpgrade;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;

import java.util.List;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final SlotMachine slotMachine;
    private final HealthBar progressBar;

    private final ScoreDisplay scoreDisplay;
    private final TurnsLeftDisplay turnsLeftDisplay;
    private final PatternDisplay patternDisplay;
    private final UpgradeSticks upgradeSticks;
    private final SpinAgainButton spinAgainButton;
    private final UpgradeBar upgradeBar;

    private final CameraShaker cameraShaker;
    private final World world;
    private final RayHandler rayHandler;
    private final WarpBackground background;
    private final BackgroundLights backgroundLights;
    private final PopupManager popupManager;

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

        popupManager = new PopupManager();
        background = new WarpBackground();
        backgroundLights = new BackgroundLights(rayHandler);
        progressBar = new HealthBar(100f);
        upgradeBar = new UpgradeBar();

        scoreDisplay = new ScoreDisplay();
        turnsLeftDisplay = new TurnsLeftDisplay();
        patternDisplay = new PatternDisplay();
        upgradeSticks = new UpgradeSticks();
        spinAgainButton = new SpinAgainButton(this::onSpinButtonPressed);

        slotMachine = new SlotMachine(app.getViewport().getWorldWidth(), app.getViewport().getWorldHeight(), rayHandler);
        cameraShaker = new CameraShaker(app);

        inputHandler = InputHandler.I();
        roundsManager = RoundsManager.I();
//        slotMachine.getReels().forEach(reel -> reel.setOnSpinFinished(() -> triggerCameraShake(0.25f)));
    }

    @Override
    public void show() {
        roundsManager.nextRound();
        scoreDisplay.resetScore();
        turnsLeftDisplay.setAppliesLeft(roundsManager.getAppliesLeft());
        turnsLeftDisplay.setSpinsLeft(roundsManager.getSpinsLeft());
        rayHandler.setAmbientLight(1f);

        //progressBar.damage(10f);
        slotMachine.getReels().get(slotMachine.getReels().size() -1).setOnSpinFinished(this::runResult);
        slotMachine.spin();
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = app.getBatch();
        handleInput(delta);
        background.render(batch, delta);
        app.getViewport().apply();

        backgroundLights.render(delta);
        cameraShaker.render(delta);

        Camera camera = app.getViewport().getCamera();
        rayHandler.setCombinedMatrix(
            camera.combined,
            camera.position.x,
            camera.position.y,
            app.getViewport().getWorldWidth(),
            app.getViewport().getWorldHeight()
        );
        rayHandler.updateAndRender();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.5f);
//        batch.draw(slotMachineShadow, slotMachine.getOriginX() - 0.5f, slotMachine.getOriginY() - 0.3f, 150 / 20f, 90 / 20f);
        batch.setColor(1f, 1f, 1f, 1f);
        //        upgradeSticks.draw(batch);
//        batch.draw(slotMachineScreen, 5.15f, 2.1f, 9.6f, 6f);
        scoreDisplay.draw(batch, delta);
        progressBar.render(batch);
//        batch.draw(cable, 0.18f, 6.075f, 14f / 25f, 34f / 25f);
//        turnsLeftDisplay.draw(batch, delta);
        patternDisplay.draw(batch, delta);
        upgradeBar.draw(batch);
        spinAgainButton.draw(batch, delta);
        slotMachine.draw(app, delta);
        popupManager.render(batch, delta);
//        batch.draw(slotMachineBorder, 5.15f, 2.1f, 9.6f, 6f);
//        batch.draw(coinSlot, 5.75f, 1.77f, 27f / 20f, 13f / 20f);
        batch.end();

        app.getUiViewport().apply();
        batch.setProjectionMatrix(app.getUiViewport().getCamera().combined);
    }


    private void handleInput(float delta) {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        app.getViewport().unproject(mouse);

        boolean leftClickPressed = Gdx.input.isButtonPressed(0);
        spinAgainButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        upgradeBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        if(upgradeBar.getHoveringUpgrade() != null) popupManager.showTooltip(upgradeBar.getHoveringUpgrade(), upgradeBar.getHoveringRectangle());
        upgradeSticks.hoveringAt(mouse);

        leftClickWasPressed = leftClickPressed;
    }

    private void runResult() {
        List<SlotMatch> matches = slotMachine.findMatches();
        if(matches.isEmpty()) {
            slotMachine.getAllSlots();
            progressBar.damage(10f);
            return;
        }

        float[] delayCounter = {0f};
        matches.forEach((slotMatch -> {
            slotMatch.slots().forEach(slot -> {
                Timer.schedule(TaskFactory.create(() -> {
                    slot.wobble();
                    slot.pulse();
                    patternDisplay.addPoints(slotMatch.symbol().baseValue());
                    popupManager.spawnNumber(Assets.I().getDigitalNumber(slotMatch.symbol().baseValue()), Assets.I().colorBlue(),
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
                    popupManager.spawnNumber(Assets.I().getDigitalNumber(slots.size()), Assets.I().colorRed(),
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
                        popupManager.spawnNumber(Assets.I().getDigitalNumber(multi), Assets.I().colorRed(),
                            upgradeBar.getRectangleByUpgrade(upgrade).x + 0.7f, 2.6f);
                    }), delayCounter[0]);
                    delayCounter[0] += 0.3f;
                });
        }));

        Timer.schedule(TaskFactory.create(() -> patternDisplay.addXMulti(1)), delayCounter[0]);
        delayCounter[0] += 0.5f;
//        Timer.schedule(TaskFactory.create(() -> {
//            scoreDisplay.addToScore(Math.round(patternDisplay.getPoints() * patternDisplay.getMulti() * patternDisplay.getXMulti()));
//            patternDisplay.resetBaseValues();
//            patternDisplay.triggerXMultAnimation();
//        }), delayCounter[0]);
    }

    private void onSpinButtonPressed() {
        slotMachine.spin();

        backgroundLights.triggerLightShake(1f);
        cameraShaker.trigger(1f);
    }

    private void onApplyButtonPressed() {
//        scoreDisplay.addToScore(slotMachine.applySelection());
        patternDisplay.reset();

        backgroundLights.triggerLightShake(1f);
        cameraShaker.trigger(1f);
    }
}
