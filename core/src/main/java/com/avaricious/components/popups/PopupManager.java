package com.avaricious.components.popups;

import com.avaricious.upgrades.Upgrade;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class PopupManager {

    private static PopupManager instance;
    public static PopupManager I() {
        return instance == null ? instance = new PopupManager() : instance;
    }

    private PopupManager() {
    }

    private final List<NumberPopup> numberPopups = new ArrayList<>();
    private final List<StatisticPopup> statisticPopups = new ArrayList<>();

    private TooltipPopup tooltipPopup;
    private boolean renderTooltip;

    public void showTooltip(Upgrade upgrade, float x, float y) {
        if(upgrade == null) return;
        renderTooltip = true;
        tooltipPopup = new TooltipPopup(upgrade.description(), new Vector2(x, y));
    }

    public void spawnNumber(int number, Color color, float x, float y, boolean manualHold) {
        numberPopups.add(new NumberPopup(number, color, x, y, false, manualHold));
    }

    public void releaseHoldingNumbers() {
        numberPopups.stream().filter(NumberPopup::isManualHold).forEach(NumberPopup::release);
    }

    public NumberPopup spawnPercentage(int number, Color color, float x, float y) {
        NumberPopup popup = new NumberPopup(number, color, x, y, true, false);
        numberPopups.add(popup);
        return popup;
    }

    public void transformLastNumber(int newValue) {
        numberPopups.get(numberPopups.size() -1).transform(newValue);
    }

    public void spawnStatisticHit(Texture texture, float x, float y) {
        statisticPopups.add(new StatisticPopup(texture, x, y));
    }

    public void draw(SpriteBatch batch, float delta) {
        for (int i = numberPopups.size() - 1; i >= 0; i--) {
            NumberPopup p = numberPopups.get(i);
            p.update(delta);
            if (p.isFinished()) {
                numberPopups.remove(i);
            }
        }
        for (NumberPopup p : numberPopups) {
            p.render(batch, delta);
        }

        for (int i = statisticPopups.size() - 1; i >= 0; i--) {
            StatisticPopup p = statisticPopups.get(i);
            p.update(delta);
            if (p.isFinished()) {
                statisticPopups.remove(i);
            }
        }
        for (StatisticPopup p : statisticPopups) {
            p.render(batch);
        }

        if(renderTooltip) tooltipPopup.render(batch);
        renderTooltip = false;
    }
}

