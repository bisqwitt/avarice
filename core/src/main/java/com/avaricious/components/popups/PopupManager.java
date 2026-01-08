package com.avaricious.components.popups;

import com.avaricious.upgrades.Upgrade;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PopupManager {

    private static PopupManager instance;
    public static PopupManager I() {
        return instance == null ? instance = new PopupManager() : instance;
    }

    private PopupManager() {
    }

    private final Array<NumberPopup> numberPopups = new Array<>();
    private final Array<StatisticPopup> statisticPopups = new Array<>();
    private TooltipPopup tooltipPopup;
    private boolean renderTooltip;

    public void renderTooltip(Upgrade upgrade, float x, float y) {
        if(upgrade == null) return;
        renderTooltip = true;
        tooltipPopup = new TooltipPopup(upgrade.description(), new Vector2(x, y));
    }

    public void spawnNumber(int number, Color color, float x, float y) {
        numberPopups.add(new NumberPopup(number, color, x, y, false));
    }

    public NumberPopup spawnPercentage(int number, Color color, float x, float y) {
        NumberPopup popup = new NumberPopup(number, color, x, y, true);
        numberPopups.add(popup);
        return popup;
    }

    public void transformLastNumber(int newValue) {
        numberPopups.get(numberPopups.size -1).transform(newValue);
    }

    public void spawnStatisticHit(Texture texture, float x, float y) {
        statisticPopups.add(new StatisticPopup(texture, x, y));
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

        for (int i = statisticPopups.size - 1; i >= 0; i--) {
            StatisticPopup p = statisticPopups.get(i);
            p.update(delta);
            if (p.isFinished()) {
                statisticPopups.removeIndex(i);
            }
        }
        for (StatisticPopup p : statisticPopups) {
            p.render(batch);
        }

        if(renderTooltip) tooltipPopup.render(batch);
        renderTooltip = false;
    }
}

