package com.avaricious.components;

import com.avaricious.Main;
import com.badlogic.gdx.graphics.Camera;

public class CameraShaker {

    private final Main app;

    private boolean cameraIsShaking = false;
    private float cameraShakeTime = 0f;
    private float cameraShakeMagnitude = 0.15f; // base magnitude in world units
    private float baseCamX;
    private float baseCamY;

    public CameraShaker(Main app) {
        this.app = app;
        Camera cam = app.getViewport().getCamera();
        baseCamX = cam.position.x;
        baseCamY = cam.position.y;
        cameraIsShaking = false;
        cameraShakeTime = 0f;
    }

    public void trigger(float strengthMultiplier) {
        Camera cam = app.getViewport().getCamera();

        // Capture the *current* camera position as base each time you trigger a shake
        baseCamX = cam.position.x;
        baseCamY = cam.position.y;

        cameraIsShaking = true;
        cameraShakeTime = 0f;
        cameraShakeMagnitude = 0.15f * strengthMultiplier;
    }

    public void render(float delta) {
        if (!cameraIsShaking) return;

        cameraShakeTime += delta;
        // total time of cam shake
        float cameraShakeDuration = 0.45f;
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
        float verticalFreq   = 8f;
        float horizontalFreq = 18f;

        float yOffset = (float) Math.sin(t * (float) Math.PI * 2f * verticalFreq)
            * cameraShakeMagnitude * fade;

        float xOffset = (float) Math.sin(t * (float) Math.PI * 2f * horizontalFreq + 0.5f)
            * (cameraShakeMagnitude * 0.5f) * fade;

        cam.position.set(baseCamX + xOffset, baseCamY + yOffset, cam.position.z);
        cam.update();
    }

}
