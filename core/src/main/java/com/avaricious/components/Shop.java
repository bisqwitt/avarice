package com.avaricious.components;

import com.avaricious.Assets;
import com.avaricious.components.buttons.Button;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Shop {

    private final Texture window;
    private final Button nextRoundButton;
    private UpgradeBar shopCardsBar;


    private boolean show = false;

    public Shop(Runnable onNextRoundPressed) {
        window = Assets.I().getShopWindow();
        nextRoundButton = new Button(() -> {
                show = false;
                onNextRoundPressed.run();
            },
            Assets.I().getNextRoundButton(), Assets.I().getNextRoundButtonPressed(), Assets.I().getNextRoundButtonHovered(),
            new Rectangle(10.5f, 1.25f, 79 / 35f, 25 / 35f), Input.Keys.ENTER);
    }

    public void draw(SpriteBatch batch, float delta) {
        if(!show) return;
        batch.draw(window, 2f, 0.5f, 225 / 20f, 163 / 20f);
        shopCardsBar.draw(batch);
        nextRoundButton.draw(batch, delta);
    }

    public void show() {
        shopCardsBar = new UpgradeBar(UpgradesManager.I().randomUpgrades(), new Rectangle(
            3f, 6f, 142 / 130f, 190 / 130f),
            1.5f, false);
        show = true;
    }

    public boolean isShowing() {
        return show;
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed, float delta) {
        if(!show) return;
        shopCardsBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        nextRoundButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
    }

}
