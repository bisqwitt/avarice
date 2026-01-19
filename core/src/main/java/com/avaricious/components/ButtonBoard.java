package com.avaricious.components;

import com.avaricious.Assets;
import com.avaricious.components.buttons.DisablableButton;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ButtonBoard {

    private final Texture buttonBoard = Assets.I().getButtonBoard();
    private final Texture buttonBoardShadow = Assets.I().getButtonBoardShadow();

    private final DisablableButton spinAgainButton;
    private final DisablableButton cashoutButton;

    private boolean visible;

    public ButtonBoard(Runnable onSpinButtonPressed, Runnable onCashoutButtonPressed) {
        spinAgainButton = new DisablableButton(onSpinButtonPressed,
            Assets.I().getSpinAgainButton(),
            Assets.I().getSpinAgainPressedButton(),
            Assets.I().getSpinAgainButton(),
            Assets.I().getSpinAgainButtonDisabled(),
            new Rectangle(8.25f, 1.75f, 79 / 35f, 25 / 35f), Input.Keys.SPACE);

        cashoutButton = new DisablableButton(onCashoutButtonPressed,
            Assets.I().getCashoutButton(),
            Assets.I().getCashoutButtonPressed(),
            Assets.I().getCashoutButton(),
            Assets.I().getCashoutButtonDisabled(),
            new Rectangle(5.2f, 1.75f, 79 / 35f, 25 / 35f), Input.Keys.ENTER);
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed) {
        spinAgainButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        cashoutButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
    }

    public void draw(SpriteBatch batch, float delta) {
        if(visible) {
            batch.setColor(1f, 1f, 1f, 0.25f);
            batch.draw(buttonBoardShadow, 5f, 1.05f, 206 / 35f, 53 / 35f);
            batch.setColor(1f, 1f, 1f, 1f);
            batch.draw(buttonBoard, 4.9f, 1.15f, 206f / 35f, 53f / 35f);
        }
        spinAgainButton.draw(batch, delta);
        cashoutButton.draw(batch, delta);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        spinAgainButton.setDisabled(!visible);
        cashoutButton.setDisabled(!visible);
    }

}
