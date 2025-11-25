package com.avaricious.components.buttons;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ButtonBoard {

    private final Texture buttonBoardTexture;
    private TextureRegion applyButtonTexture;
    private TextureRegion spinButtonTexture;

    private final Rectangle applyButton;
    private final Rectangle spinButton;

    private final Runnable onApplyPressed;
    private final Runnable onSpinPressed;

    private boolean hoveringApply = false;
    private boolean hoveringSpin = false;
    private boolean hoverApplyJustEntered = false;
    private boolean hoverSpinJustEntered = false;
    private float hoverAnimTime = 0f;

    public ButtonBoard(Runnable onApplyPressed, Runnable onSpinPressed) {
        this.onApplyPressed = onApplyPressed;
        this.onSpinPressed = onSpinPressed;

        Assets assetManager = Assets.I();
        buttonBoardTexture = assetManager.getButtonBoard();
        applyButtonTexture = new TextureRegion(assetManager.getApplyButton());
        spinButtonTexture = new TextureRegion(assetManager.getSpinButton());

        applyButton = new Rectangle(9.55f, 2.6f, 2.15f, 0.95f);
        spinButton = new Rectangle(12.175f, 2.6f, 1f, 0.88f);
    }

    public void draw(SpriteBatch batch, float delta) {
//        batch.draw(buttonBoardTexture, 7.55f, 0.3f, 4.6f, 1.36f);
        drawButton(batch, delta, "apply");
        drawButton(batch, delta, "spin");
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed) {
        boolean isHoveringNow = applyButton.contains(mouse.x, mouse.y);
        hoverApplyJustEntered = !hoveringApply && isHoveringNow; // mouse entered
        hoveringApply = isHoveringNow; // update state

        boolean isHoveringSpinNow = spinButton.contains(mouse.x, mouse.y);
        hoverSpinJustEntered = !hoveringSpin && isHoveringSpinNow;
        hoveringSpin = isHoveringSpinNow;

        if(pressed && !wasPressed) {
            if (applyButton.contains(mouse.x, mouse.y)) setApplyPressed(true);
            if (spinButton.contains(mouse.x, mouse.y)) setSpinPressed(true);
        }else if (!pressed && wasPressed) {
            setApplyPressed(false);
            setSpinPressed(false);
            if (applyButton.contains(mouse.x, mouse.y)) onApplyPressed.run();
            if (spinButton.contains(mouse.x, mouse.y)) onSpinPressed.run();
        }
    }

    private void drawButton(SpriteBatch batch, float delta, String type) {
        float x = 9.05f;
        float y = 2.3f;
        float w = 4.6f;
        float h = 1.36f;

        TextureRegion texture = type.equals("apply") ? applyButtonTexture : spinButtonTexture;
        boolean hoverJustEntered = type.equals("apply") ? hoverApplyJustEntered : hoverSpinJustEntered;
        boolean hovering = type.equals("apply") ? hoveringApply : hoveringSpin;
        float wiggleAmount = type.equals("apply") ? 0.01f : 0.025f;


        if(hoverJustEntered) hoverAnimTime = 0f;

        float scale = 1f;
        if(hovering && hoverAnimTime < 0.25f) {
            hoverAnimTime += delta;

            float t = hoverAnimTime / 0.25f;
            float wiggle = (float) Math.sin(t * Math.PI * 3f) * (1f - t) * wiggleAmount;
            scale = 1f + wiggle;
        }

        float originX = w / 2f;
        float originY = h / 2f;
        batch.draw(texture,
            x + w/2f - originX * scale,
            y + h/2f - originY * scale,
            originX, originY,
            w, h,
            scale, scale,
            0);
    }
    public Rectangle getApplyButton() {
        return applyButton;
    }

    public Rectangle getSpinButton() {
        return spinButton;
    }

    public void setApplyPressed(boolean pressed) {
        applyButtonTexture = new TextureRegion(pressed ? Assets.I().getApplyButtonPressed() : Assets.I().getApplyButton());
    }

    public void setSpinPressed(boolean pressed) {
        spinButtonTexture = new TextureRegion(pressed ? Assets.I().getSpinButtonPressed() : Assets.I().getSpinButton());
    }
}
