package com.avaricious.components.popups;

import com.avaricious.upgrades.Upgrade;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PopupManager {
    private final Array<NumberPopup> numberPopups = new Array<>();
    private TooltipPopup tooltipPopup;
    private boolean renderTooltip;

    public void showTooltip(Upgrade upgrade, Rectangle rectangle) {
        if(upgrade == null) return;
        renderTooltip = true;
        tooltipPopup = new TooltipPopup(upgrade.description(), rectangle);
    }

    public void spawnNumber(Texture numberTexture, Color color, float x, float y) {
        numberPopups.add(new NumberPopup(numberTexture, color, x, y));
    }

    public void render(SpriteBatch batch, float delta) {
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

