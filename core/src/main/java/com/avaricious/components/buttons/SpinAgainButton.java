package com.avaricious.components.buttons;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;

public class SpinAgainButton {

    private TextureRegion buttonTexture;
    private Rectangle buttonRectangle;

    private boolean hovering = false;
    private boolean hoverJustEntered = false;
    private float hoverAnimTime = 0f;

    private final Runnable onButtonPressedRunnable;

    public SpinAgainButton(Runnable onButtonPressedRunnable) {
        this.onButtonPressedRunnable = onButtonPressedRunnable;

        buttonTexture = new TextureRegion(Assets.I().getSpinAgainButton());
        buttonRectangle = new Rectangle(12.5f, 2.4f, 79 / 50f, 54 / 50f);
    }

    public void draw(SpriteBatch batch, float delta) {
        if(hoverJustEntered) hoverAnimTime = 0f;

        float scale = 1f;
        if(hovering && hoverAnimTime < 0.25f) {
            hoverAnimTime += delta;

            float t = hoverAnimTime / 0.25f;
            float wiggle = (float) Math.sin(t * Math.PI * 3f) * (1f - t) * 0.02f;
            scale = 1f + wiggle;
        }

        float originX = buttonRectangle.width / 2f;
        float originY = buttonRectangle.height / 2f;
        batch.draw(buttonTexture,
            buttonRectangle.x + buttonRectangle.width / 2f - originX * scale,
            buttonRectangle.y + buttonRectangle.height / 2f - originY * scale,
            originX, originY,
            buttonRectangle.width, buttonRectangle.height,
            scale, scale,
            0);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed) {
        boolean isHoveringNow = buttonRectangle.contains(mouse.x, mouse.y);
        hoverJustEntered = !hovering && isHoveringNow; // mouse entered
        hovering = isHoveringNow; // update state

        if(pressed && !wasPressed) {
            if(buttonRectangle.contains(mouse.x, mouse.y))
                buttonTexture = new TextureRegion(Assets.I().getSpinAgainPressedButton());
        } else if(!pressed && wasPressed) {
            buttonTexture = new TextureRegion(Assets.I().getSpinAgainButton());
            if(buttonRectangle.contains(mouse.x, mouse.y)) onButtonPressedRunnable.run();
        }
    }

}
