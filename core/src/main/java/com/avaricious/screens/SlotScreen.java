package com.avaricious.screens;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.RayHandler;
import com.avaricious.*;
import com.avaricious.components.*;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.Symbol;
import com.avaricious.components.slot.SymbolManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final SlotMachine slotMachine;
    private final ScoreDisplay scoreDisplay;
    private final TurnsLeftDisplay turnsLeftDisplay;
    private final PatternDisplay patternDisplay;
    private final UpgradeSticks upgradeSticks;
    private final ButtonBoard buttonBoard;
    private final RoundsManager roundsManager;
    private final Vector2 mouse = new Vector2();
    private boolean wasPressed = false;

    private final Texture slotMachineBorder;
    private final Texture slotMachineScreen;
    private final BitmapFont bigFont;
    private final ShapeRenderer shapeRenderer;

    private final World world;
    private final RayHandler rayHandler;
    private final ConeLight[] topLights = new ConeLight[5];
    private float[] baseLightX;
    private float[] baseLightY;

    // Light shake
    private boolean lightIsShaking = false;
    private float lightShakeTime = 0f;
    private float lightShakeStrength = 0f;

    // Camera shake
    private boolean cameraIsShaking = false;
    private float cameraShakeTime = 0f;
    private float cameraShakeDuration = 0.45f;  // total time of cam shake
    private float cameraShakeMagnitude = 0.15f; // base magnitude in world units
    private float baseCamX;
    private float baseCamY;

    public SlotScreen(Main app) {
        this.app = app;
        slotMachineBorder = Assets.I().getSlotMachineBorder();
        slotMachineScreen = Assets.I().getSlotMachineScreen();

        this.world = new World(new Vector2(0, 0), true);
        rayHandler = new RayHandler(world);

        bigFont = Assets.I().getBigFont();

        shapeRenderer = new ShapeRenderer();
        slotMachine = new SlotMachine(app.getViewport().getWorldWidth(), app.getViewport().getWorldHeight(), rayHandler);
        upgradeSticks = new UpgradeSticks();
        scoreDisplay = new ScoreDisplay();
        turnsLeftDisplay = new TurnsLeftDisplay();
        patternDisplay = new PatternDisplay();
        buttonBoard = new ButtonBoard();
        roundsManager = RoundsManager.I();
    }

    @Override
    public void show() {
        roundsManager.nextRound();
        scoreDisplay.resetScore();
        turnsLeftDisplay.setAppliesLeft(roundsManager.getAppliesLeft());
        turnsLeftDisplay.setSpinsLeft(roundsManager.getSpinsLeft());

        // Store base camera position for shake
        Camera cam = app.getViewport().getCamera();
        baseCamX = cam.position.x;
        baseCamY = cam.position.y;

        // Reset shake states
        lightIsShaking = false;
        lightShakeTime = 0f;
        lightShakeStrength = 0f;

        cameraIsShaking = false;
        cameraShakeTime = 0f;
        cameraShakeMagnitude = 0.15f;

        rayHandler.setAmbientLight(0.7f);

        float startX = 2f;
        float stepX  = 3f;
        float y      = 11f;
        float distance = 14f;
        float direction = 270f;
        float coneDegrees = 20f;

        baseLightX = new float[topLights.length];
        baseLightY = new float[topLights.length];

        for (int i = 0; i < topLights.length; i++) {
            float x = startX + i * stepX;
            baseLightX[i] = x;
            baseLightY[i] = y;

            topLights[i] = new ConeLight(
                rayHandler,
                120,
                Assets.I().lightColor(),
                distance,
                x, y,
                direction,
                coneDegrees
            );
        }
        slotMachine.clearSelection();
        slotMachine.spin();
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = app.getBatch();
        handleInput();
        ScreenUtils.clear(0.396f, 0.137f, 0.141f, 1f);

        // First apply the viewport so the camera is in its default place
        app.getViewport().apply();

        // Then update shakes (they will modify this camera)
        updateLightShake(delta);
        updateCameraShake(delta);


        Camera camera = app.getViewport().getCamera();
        rayHandler.setCombinedMatrix(
            camera.combined,
            camera.position.x,
            camera.position.y,
            app.getViewport().getWorldWidth(),
            app.getViewport().getWorldHeight()
        );
        rayHandler.updateAndRender();

        // Now draw world using the already-shaken camera
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        upgradeSticks.draw(batch);
        batch.draw(slotMachineScreen, 5.15f, 2.1f, 9.6f, 6f);
        scoreDisplay.draw(batch);
        turnsLeftDisplay.draw(batch);
        patternDisplay.draw(batch);
        buttonBoard.draw(batch, delta);
        batch.end();

        batch.begin();
        slotMachine.draw(app, delta);
        batch.draw(slotMachineBorder, 5.15f, 2.1f, 9.6f, 6f);
        batch.end();

        // ... rest of your code stays the same ...

        app.getUiViewport().apply();
        batch.setProjectionMatrix(app.getUiViewport().getCamera().combined);
        batch.begin();
        scoreDisplay.draw(batch);
        batch.end();
    }


    private void handleInput() {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        app.getViewport().unproject(mouse);

        Rectangle slotBounds = slotMachine.getBounds();
        boolean pressed = Gdx.input.isButtonPressed(0);

        buttonBoard.handleInput(mouse);

        if (pressed && !wasPressed) {
            if (buttonBoard.getApplyButton().contains(mouse.x, mouse.y)) buttonBoard.setApplyPressed(true);
            if (buttonBoard.getSpinButton().contains(mouse.x, mouse.y)) buttonBoard.setSpinPressed(true);

            if (slotBounds.contains(mouse.x, mouse.y)) {
                int col = (int)((mouse.x - slotBounds.x) / (slotMachine.getCellW() + slotMachine.getSpacingX()));
                int row = (int)((mouse.y - slotBounds.y) / (slotMachine.getCellH() + slotMachine.getSpacingY()));

                if (col >= 0 && col < slotMachine.getCols() &&
                    row >= 0 && row < slotMachine.getRows()) {
                    slotMachine.selectSymbolAt(col, row);
                    patternDisplay.setPattern(slotMachine.getScoreFormula());
                }
            }
        }

        if (!pressed && wasPressed) {
            buttonBoard.setApplyPressed(false);
            buttonBoard.setSpinPressed(false);
            if (buttonBoard.getApplyButton().contains(mouse.x, mouse.y)) onApplyButtonPressed();
            if (buttonBoard.getSpinButton().contains(mouse.x, mouse.y)) onSpinButtonPressed();
        }
        slotMachine.hoveringAt(mouse);
        upgradeSticks.hoveringAt(mouse);

        wasPressed = pressed;
    }

    private void onSpinButtonPressed() {
        if (roundsManager.getSpinsLeft() == 0) return;
        slotMachine.spin();
        roundsManager.minusOneSpin();
        turnsLeftDisplay.setSpinsLeft(roundsManager.getSpinsLeft());
        patternDisplay.setPattern("");

        // Light shake impulse
        lightIsShaking = true;
        lightShakeStrength = Math.min(lightShakeStrength + 1.0f, 1.5f);

        // Camera shake impulse (stronger)
        triggerCameraShake(1.0f);
    }

    private void onApplyButtonPressed() {
        if (roundsManager.getAppliesLeft() == 0) return;
        scoreDisplay.addToScore(slotMachine.applySelection());
        roundsManager.minusOneHand();
        turnsLeftDisplay.setAppliesLeft(roundsManager.getAppliesLeft());
        patternDisplay.setPattern("");

        // Light shake impulse (smaller)
        lightIsShaking = true;
        lightShakeStrength = Math.min(lightShakeStrength + 0.5f, 1.5f);

        // Optional: smaller camera bump
        triggerCameraShake(0.5f);
    }

    private void triggerCameraShake(float strengthMultiplier) {
        Camera cam = app.getViewport().getCamera();

        // Capture the *current* camera position as base each time you trigger a shake
        baseCamX = cam.position.x;
        baseCamY = cam.position.y;

        cameraIsShaking = true;
        cameraShakeTime = 0f;
        cameraShakeMagnitude = 0.15f * strengthMultiplier;
    }

    private void updateLightShake(float delta) {
        if (!lightIsShaking) return;

        lightShakeTime += delta;

        float baseDirection = 270f;

        // Max angle swing at full lightShakeStrength
        float maxAngle = 10f;

        // How quickly “energy” decays per second
        float strengthDecayPerSecond = 0.25f; // smaller = longer tail

        // Oscillation speed (how fast it swings left/right)
        float frequency = 6f;

        // 1) Decay shake strength over time
        lightShakeStrength -= strengthDecayPerSecond * delta;
        if (lightShakeStrength <= 0f) {
            lightShakeStrength = 0f;
            lightIsShaking = false;
            // Reset lights to their default pose
            for (int i = 0; i < topLights.length; i++) {
                ConeLight light = topLights[i];
                if (light == null) continue;

                float baseX = baseLightX[i];
                float baseY = baseLightY[i];

                light.setPosition(baseX, baseY);
                light.setDirection(baseDirection);
            }
            return;
        }

        // 2) Compute current angle amplitude based on remaining strength
        float amplitude = maxAngle * lightShakeStrength;

        // 3) Animate each light
        for (int i = 0; i < topLights.length; i++) {
            ConeLight light = topLights[i];
            if (light == null) continue;

            float phaseOffset = i * 0.5f;

            float angleOffset =
                (float) Math.sin(lightShakeTime * frequency + phaseOffset) * amplitude;

            // Horizontal sway
            float swayRadius = 0.3f;
            float offsetX =
                (float) Math.sin(lightShakeTime * frequency + phaseOffset) * swayRadius * lightShakeStrength;

            // Small vertical bob
            float swayY = 0.1f;
            float offsetY =
                (float) Math.cos(lightShakeTime * frequency + phaseOffset) * swayY * lightShakeStrength;

            float baseX = baseLightX[i];
            float baseY = baseLightY[i];

            light.setPosition(baseX + offsetX, baseY + offsetY);
            light.setDirection(baseDirection + angleOffset);
        }
    }

    private void updateCameraShake(float delta) {
        if (!cameraIsShaking) return;

        cameraShakeTime += delta;
        float t = cameraShakeTime / cameraShakeDuration;

        Camera cam = app.getViewport().getCamera();

        if (t >= 1f) {
            cameraIsShaking = false;
            cam.position.set(baseCamX, baseCamY, cam.position.z);
            cam.update();
            return;
        }

        // Fade out over time (ease-out)
        float fade = (1f - t);
        fade *= fade; // (1 - t)^2 for smoother tail

        // Stronger vertical shake, slight horizontal
        float verticalFreq   = 18f;
        float horizontalFreq = 11f;

        float yOffset = (float) Math.sin(t * (float) Math.PI * 2f * verticalFreq)
            * cameraShakeMagnitude * fade;

        float xOffset = (float) Math.sin(t * (float) Math.PI * 2f * horizontalFreq + 0.5f)
            * (cameraShakeMagnitude * 0.5f) * fade;

        cam.position.set(baseCamX + xOffset, baseCamY + yOffset, cam.position.z);
        cam.update();
    }
}
