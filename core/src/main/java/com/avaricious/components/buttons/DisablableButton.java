package com.avaricious.components.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class DisablableButton extends Button {

    private final TextureRegion disabledTexture;

    public DisablableButton(Runnable onButtonPressedRunnable, Texture defaultButtonTexture, Texture pressedButtonTexture, Texture hoveredButtonTexture, Texture disabledTexture, Rectangle buttonRectangle, int key) {
        super(onButtonPressedRunnable, defaultButtonTexture, pressedButtonTexture, hoveredButtonTexture, buttonRectangle, key);
        this.disabledTexture = new TextureRegion(disabledTexture);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, boolean disable) {
        if(disable) currentTexture = disabledTexture;
        else super.handleInput(mouse, pressed, wasPressed);
    }

}
