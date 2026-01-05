package com.avaricious.components.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class DisablableButton extends Button {

    private final TextureRegion disabledTexture;
    private boolean disabled;

    public DisablableButton(Runnable onButtonPressedRunnable, Texture defaultButtonTexture, Texture pressedButtonTexture, Texture hoveredButtonTexture, Texture disabledTexture, Rectangle buttonRectangle, int key) {
        super(onButtonPressedRunnable, defaultButtonTexture, pressedButtonTexture, hoveredButtonTexture, buttonRectangle, key);
        this.disabledTexture = new TextureRegion(disabledTexture);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed) {
        super.handleInput(mouse, pressed, wasPressed);
        if(disabled) {
            currentTexture = disabledTexture;
            wasHovered = false;
        }
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        if(!disabled) currentTexture = defaultButtonTexture;
    }

    public boolean isDisabled() {
        return disabled;
    }

}
