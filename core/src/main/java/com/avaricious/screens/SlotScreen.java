package com.avaricious.screens;

import box2dLight.RayHandler;
import com.avaricious.*;
import com.avaricious.components.*;
import com.avaricious.components.SmokeBackground;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.components.displays.TurnsLeftDisplay;
import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.SlotMachine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final SlotMachine slotMachine;
    private final TimedProgressBar progressBar;
    private final Texture slotMachineBorder;
    private final Texture slotMachineScreen;
    private final Texture cable;
    private final Texture coinSlot;

    private final ScoreDisplay scoreDisplay;
    private final TurnsLeftDisplay turnsLeftDisplay;
    private final PatternDisplay patternDisplay;
    private final UpgradeSticks upgradeSticks;
    private final ButtonBoard buttonBoard;

    private final CameraShaker cameraShaker;
    private final World world;
    private final RayHandler rayHandler;
    private final WarpBackground background;
    private final BackgroundLights backgroundLights;
    private final PopupManager popupManager;

    private final RoundsManager roundsManager;
    private final Vector2 mouse = new Vector2();
    private boolean wasPressed = false;

    public SlotScreen(Main app) {
        this.app = app;
        this.world = new World(new Vector2(0, 0), true);
//        RayHandler.setGammaCorrection(true);
//        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(world);

        popupManager = new PopupManager();
        background = new WarpBackground();
        backgroundLights = new BackgroundLights(rayHandler);
        slotMachineBorder = Assets.I().getSlotMachineBorder();
        slotMachineScreen = Assets.I().getSlotMachineScreen();
        cable = Assets.I().getCable();
        coinSlot = Assets.I().getCoinSlot();
        progressBar = new TimedProgressBar(5f);

        scoreDisplay = new ScoreDisplay();
        turnsLeftDisplay = new TurnsLeftDisplay();
        patternDisplay = new PatternDisplay();
        upgradeSticks = new UpgradeSticks();
        buttonBoard = new ButtonBoard(this::onApplyButtonPressed, this::onSpinButtonPressed);

        slotMachine = new SlotMachine(app.getViewport().getWorldWidth(), app.getViewport().getWorldHeight(), rayHandler);
        cameraShaker = new CameraShaker(app);

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

        slotMachine.getReels().get(slotMachine.getReels().size() -1).setOnSpinFinished(() -> {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    calcSelection();
                    slotMachine.lockHoverAndSelect();
                }
            }, 5);
        });

        slotMachine.clearSelection();
        slotMachine.spin();
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = app.getBatch();
        handleInput();
        background.render(batch, delta);
        app.getViewport().apply();

        popupManager.update(delta);

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
//        upgradeSticks.draw(batch);
//        batch.draw(slotMachineScreen, 5.15f, 2.1f, 9.6f, 6f);
        scoreDisplay.draw(batch, delta);
//        progressBar.render(batch, delta);
//        batch.draw(cable, 0.18f, 6.075f, 14f / 25f, 34f / 25f);
//        turnsLeftDisplay.draw(batch, delta);
        patternDisplay.draw(batch, delta);
//        buttonBoard.draw(batch, delta);
        slotMachine.draw(app, delta);
        popupManager.render(batch);
//        batch.draw(slotMachineBorder, 5.15f, 2.1f, 9.6f, 6f);
//        batch.draw(coinSlot, 5.75f, 1.77f, 27f / 20f, 13f / 20f);
        batch.end();

        app.getUiViewport().apply();
        batch.setProjectionMatrix(app.getUiViewport().getCamera().combined);
    }


    private void handleInput() {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        app.getViewport().unproject(mouse);

        Rectangle slotBounds = slotMachine.getBounds();
        boolean pressed = Gdx.input.isButtonPressed(0);

        buttonBoard.handleInput(mouse, pressed, wasPressed);

        if (pressed && !wasPressed) {
            if (slotBounds.contains(mouse.x, mouse.y)) {
                int col = (int)((mouse.x - slotBounds.x) / (slotMachine.getCellW() + slotMachine.getSpacingX()));
                int row = (int)((mouse.y - slotBounds.y) / (slotMachine.getCellH() + slotMachine.getSpacingY()));

                if (col >= 0 && col < slotMachine.getCols() &&
                    row >= 0 && row < slotMachine.getRows()) {
                    slotMachine.selectSymbolAt(col, row);
//                    patternDisplay.setPattern(slotMachine.getScoreFormula());
                }
            }
        }
        slotMachine.hoveringAt(mouse);
        upgradeSticks.hoveringAt(mouse);

        wasPressed = pressed;
    }

    private void calcSelection() {
        float[] delayCounter = {0f};
        slotMachine.getSelectedSlots().forEach(((symbol, slots) -> {
            slots.forEach(slot -> {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        slot.wobble();
                        slot.pulse();
                        patternDisplay.addPoints(symbol.baseValue());
                        popupManager.spawn(Assets.I().getDigitalNumber(symbol.baseValue()), Assets.I().colorBlue(), 10f, 8f);
                    }
                }, delayCounter[0]);
                delayCounter[0] += 0.5f;
            });

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    slots.forEach(slot -> {
                        slot.wobble();
                        slot.pulse();
                    });
                    patternDisplay.addMulti(slots.size());
                    popupManager.spawn(Assets.I().getDigitalNumber(slotMachine.countSymbol(symbol)), Assets.I().colorRed(), 10f, 8f);
                }
            },  delayCounter[0]);
            delayCounter[0] += 0.5f;
        }));
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                onApplyButtonPressed();
            }
        }, delayCounter[0]);
    }

    private void onSpinButtonPressed() {
        if (roundsManager.getSpinsLeft() == 0) return;
        slotMachine.spin();
        roundsManager.minusOneSpin();
        turnsLeftDisplay.setSpinsLeft(roundsManager.getSpinsLeft());
        patternDisplay.setPattern("");

        backgroundLights.triggerLightShake(1f);
        cameraShaker.trigger(1f);
    }

    private void onApplyButtonPressed() {
        if (roundsManager.getAppliesLeft() == 0) return;
        scoreDisplay.addToScore(slotMachine.applySelection());
        roundsManager.minusOneHand();
        turnsLeftDisplay.setAppliesLeft(roundsManager.getAppliesLeft());
        patternDisplay.setPattern("");

        backgroundLights.triggerLightShake(1f);
        cameraShaker.trigger(1f);
    }
}
