package com.avaricious.components;

import com.avaricious.Assets;
import com.avaricious.CreditManager;
import com.avaricious.CreditNumber;
import com.avaricious.components.buttons.Button;
import com.avaricious.upgrades.UpgradesManager;
import com.avaricious.upgrades.bars.JokerUpgradeBar;
import com.avaricious.upgrades.bars.JokerUpgradeBarWithPrices;
import com.avaricious.upgrades.bars.UpgradeBar;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Shop {

    private final Texture window;
    private final Button returnButton;
    private final Button rerollButton;

    private final CreditNumber creditScore;
    private final UpgradeBar shopCardsBar;

    private boolean show = false;

    public Shop(Runnable onExit) {
        window = Assets.I().getShopWindow();

        creditScore = new CreditNumber(0,
            new Rectangle(3.1f, 5.1f, 0.32f, 0.56f), 0.35f);
        shopCardsBar = new JokerUpgradeBarWithPrices(UpgradesManager.I().randomUpgrades(), new Rectangle(
            3f, 6f, 142 / 130f, 190 / 130f),
            1.5f, false);

        rerollButton = new Button(() -> {
                shopCardsBar.loadUpgrades(UpgradesManager.I().randomUpgrades());
                CreditManager.I().pay(3);
                creditScore.setScore(CreditManager.I().getCredits());
            },
            Assets.I().getRerollButton(), Assets.I().getRerollButtonPressed(), Assets.I().getRerollButtonHovered(),
            new Rectangle(10f, 4f, 79 / 35f, 25 / 35f), Input.Keys.SPACE);
        returnButton = new Button(() -> {
                show = false;
            },
            Assets.I().getReturnButton(), Assets.I().getReturnButtonPressed(), Assets.I().getReturnButtonHovered(),
            new Rectangle(10.5f, 1.25f, 79 / 35f, 25 / 35f), Input.Keys.ENTER);
    }

    public void draw(SpriteBatch batch, float delta) {
        if(!show) return;
        batch.draw(window, 2f, 0.5f, 225 / 20f, 163 / 20f);
        shopCardsBar.draw(batch);
        returnButton.draw(batch, delta);
        rerollButton.draw(batch, delta);
        creditScore.draw(batch, delta);
    }

    public void show() {
        shopCardsBar.loadUpgrades(UpgradesManager.I().randomUpgrades());
        show = true;
        creditScore.setScore(CreditManager.I().getCredits());
    }

    public boolean isShowing() {
        return show;
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed, float delta) {
        if(!show) return;
        shopCardsBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        returnButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        rerollButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
    }

}
