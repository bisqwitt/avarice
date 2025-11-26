package com.avaricious.components.buttons;

import com.avaricious.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private boolean spaceWasPressed;

    private final Runnable onButtonPressedRunnable;

    public SpinAgainButton(Runnable onButtonPressedRunnable) {
        this.onButtonPressedRunnable = onButtonPressedRunnable;

        buttonTexture = new TextureRegion(Assets.I().getSpinAgainButton());
        buttonRectangle = new Rectangle(12.5f, 2.4f, 79 / 50f, 54 / 50f);
    }

    public void draw(SpriteBatch batch, float delta) {

        float originX = buttonRectangle.width / 2f;
        float originY = buttonRectangle.height / 2f;
        batch.draw(buttonTexture,
            buttonRectangle.x + buttonRectangle.width / 2f - originX,
            buttonRectangle.y + buttonRectangle.height / 2f - originY,
            originX, originY,
            buttonRectangle.width, buttonRectangle.height,
            1, 1,
            0);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed) {
        hovering = buttonRectangle.contains(mouse.x, mouse.y); // update state

        if(pressed && !wasPressed) {
            if(buttonRectangle.contains(mouse.x, mouse.y))
                buttonTexture = new TextureRegion(Assets.I().getSpinAgainPressedButton());
        } else if(!pressed && wasPressed) {
            buttonTexture = new TextureRegion(Assets.I().getSpinAgainButton());
            if(buttonRectangle.contains(mouse.x, mouse.y)) onButtonPressedRunnable.run();
        }

        boolean spacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        if(spacePressed && !spaceWasPressed) buttonTexture = new TextureRegion(Assets.I().getSpinAgainPressedButton());
        if(!spacePressed && spaceWasPressed) {
            buttonTexture = new TextureRegion(Assets.I().getSpinAgainButton());
            onButtonPressedRunnable.run();
        }

        spaceWasPressed = spacePressed;
    }

}
