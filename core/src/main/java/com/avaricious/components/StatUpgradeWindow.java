package com.avaricious.components;

import com.avaricious.Assets;
import com.avaricious.stats.statupgrades.StatUpgrade;
import com.avaricious.upgrades.bars.StatUpgradeBar;
import com.avaricious.upgrades.bars.UpgradeBar;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class StatUpgradeWindow {

    private final Texture window = Assets.I().getStatUpgradeWindow();
    private final Texture shadow = Assets.I().getStatUpgradeWindowShadow();
    private final TextureRegion levelUpTxt = new TextureRegion(Assets.I().getLevelUpTxt());
    private boolean show = false;

    private static final float PULSE_SPEED = 3.5f;
    private static final float PULSE_AMPLITUDE = 0.05f;
    private float levelUpAnimTime = 0f;
    private float levelUpTxtScale = 1;

    private final UpgradeBar upgradeBar = new StatUpgradeBar(randomStatUpgrades(),
        new Rectangle(5.6f, 2.6f, 1.25f, 1.25f));

    public StatUpgradeWindow(Runnable onExit) {
        upgradeBar.setOnUpgradeClickedAndAnimationEnded(() -> {
            show = false;
            onExit.run();
        });
    }

    private List<StatUpgrade> randomStatUpgrades() {
        return List.of(StatUpgrade.newRandom(), StatUpgrade.newRandom(), StatUpgrade.newRandom());
    }

    public void draw(SpriteBatch batch, float delta) {
        if(!show) return;

        levelUpAnimTime += delta;
        levelUpTxtScale = 1f + (float)Math.sin(levelUpAnimTime * PULSE_SPEED) * PULSE_AMPLITUDE;

        batch.setColor(1f, 1f, 1f, 0.25f);
        batch.draw(shadow, 4.1f, 0.85f, 225f / 30f, 163f / 30f);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(window, 4.1f, 1f, 225f / 30f, 163f / 30f);
        upgradeBar.draw(batch);

        float x = 5.9f;
        float y = 4.6f;
        float w = 55f / 15f;
        float h = 13f / 15f;

        batch.draw(
            levelUpTxt,
            x, y,
            w / 2f, h / 2f,
            w, h,
            levelUpTxtScale, levelUpTxtScale,
            0f
        );
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
