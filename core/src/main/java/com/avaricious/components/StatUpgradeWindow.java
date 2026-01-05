package com.avaricious.components;

import com.avaricious.Assets;
import com.avaricious.stats.statupgrades.StatUpgrade;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class StatUpgradeWindow {

    private final Texture window;
    private boolean show = false;

    private final UpgradeBar upgradeBar;

    public StatUpgradeWindow() {
        window = Assets.I().getStatUpgradeWindow();

        upgradeBar = new UpgradeBar(randomStatUpgrades(), new Rectangle(
            8.75f, 5.4f, 1.25f, 1.25f),
            1.5f, false);
        upgradeBar.setOnUpgradeClicked(() -> show = false);
    }

    private List<StatUpgrade> randomStatUpgrades() {
        return List.of(StatUpgrade.newRandom(), StatUpgrade.newRandom(), StatUpgrade.newRandom());
    }

    public void draw(SpriteBatch batch, float delta) {
        if(!show) return;
        batch.draw(window, 7.25f, 2f, 225f / 30f, 163f / 30f);
        upgradeBar.draw(batch);
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed, float delta) {
        if(!show) return;
        upgradeBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
    }

    public void show() {
        upgradeBar.loadUpgrades(randomStatUpgrades());
        show = true;
    }

}
