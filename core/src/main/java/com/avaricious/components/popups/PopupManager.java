package com.avaricious.components.popups;

import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import javax.swing.*;

public class PopupManager {

    private static PopupManager instance;
    public static PopupManager I() {
        return instance == null ? instance = new PopupManager() : instance;
    }

    private PopupManager() {
    }

    private final Array<NumberPopup> numberPopups = new Array<>();
    private TooltipPopup tooltipPopup;
    private boolean renderTooltip;

    public void showTooltip(Upgrade upgrade, float x, float y) {
        if(upgrade == null) return;
        renderTooltip = true;
        tooltipPopup = new TooltipPopup(upgrade.description(), new Vector2(x, y));
    }

    public void spawnNumber(Texture numberTexture, Color color, float x, float y) {
        numberPopups.add(new NumberPopup(numberTexture, color, x, y));
    }

    public void draw(SpriteBatch batch, float delta) {
        for (int i = numberPopups.size - 1; i >= 0; i--) {
            NumberPopup p = numberPopups.get(i);
            p.update(delta);
            if (p.isFinished()) {
                numberPopups.removeIndex(i);
            }
        }

        for (NumberPopup p : numberPopups) {
            p.render(batch);
        }

        if(renderTooltip) tooltipPopup.render(batch);

        renderTooltip = false;
    }
}

