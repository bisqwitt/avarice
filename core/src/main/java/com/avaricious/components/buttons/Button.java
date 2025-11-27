package com.avaricious.components.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Button {

    private TextureRegion currentTexture;
    private Rectangle buttonRectangle;

    private boolean wasHovered;
    private boolean spaceWasPressed;

    private final Runnable onButtonPressedRunnable;
    private final TextureRegion defaultButtonTexture;
    private final TextureRegion pressedButtonTexture;
    private final TextureRegion hoveredButtonTexture;
    private final int key;

    public Button(Runnable onButtonPressedRunnable,
                  Texture defaultButtonTexture,
                  Texture pressedButtonTexture,
                  Texture hoveredButtonTexture,
                  Rectangle buttonRectangle,
                  int key) {
        this.onButtonPressedRunnable = onButtonPressedRunnable;

        this.defaultButtonTexture = new TextureRegion(defaultButtonTexture);
        this.pressedButtonTexture = new TextureRegion(pressedButtonTexture);
        this.hoveredButtonTexture = new TextureRegion(hoveredButtonTexture);
        this.key = key;

        currentTexture = this.defaultButtonTexture;
        this.buttonRectangle = buttonRectangle;
    }

    public void draw(SpriteBatch batch, float delta) {
        float originX = buttonRectangle.width / 2f;
        float originY = buttonRectangle.height / 2f;
        batch.draw(currentTexture,
            buttonRectangle.x + buttonRectangle.width / 2f - originX,
            buttonRectangle.y + buttonRectangle.height / 2f - originY,
            originX, originY,
            buttonRectangle.width, buttonRectangle.height,
            1, 1,
            0);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed) {
        boolean hovering = buttonRectangle.contains(mouse.x, mouse.y);
        if(hovering && !wasHovered) currentTexture = hoveredButtonTexture; // update state
        else if(!hovering && wasHovered) currentTexture = defaultButtonTexture;

        if(pressed && !wasPressed) {
            if(buttonRectangle.contains(mouse.x, mouse.y))
                currentTexture = pressedButtonTexture;
        } else if(!pressed && wasPressed) {
            currentTexture = hovering ? hoveredButtonTexture : defaultButtonTexture;
            if(hovering) onButtonPressedRunnable.run();
        }

        boolean spacePressed = Gdx.input.isKeyPressed(key);
        if(spacePressed && !spaceWasPressed) currentTexture = pressedButtonTexture;
        if(!spacePressed && spaceWasPressed) {
            currentTexture = defaultButtonTexture;
            onButtonPressedRunnable.run();
        }

        wasHovered = hovering;
        spaceWasPressed = spacePressed;
    }

}
