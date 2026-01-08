package com.avaricious.upgrades.bars;

import com.avaricious.Assets;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.stats.statupgrades.StatUpgrade;
import com.avaricious.upgrades.Upgrade;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.Collections;
import java.util.List;

public class StatUpgradeBar extends UpgradeBar{

    public StatUpgradeBar(List<? extends Upgrade> upgrades) {
        super(upgrades, new Rectangle(
                8.75f, 5.4f, 1.25f, 1.25f),
            1.5f, false);
    }

    @Override
    protected void onCardClicked(Upgrade clickedUpgrade) {
        clickedUpgrade.apply();
        cardBounds.keySet().retainAll(Collections.singleton(clickedUpgrade));
        jokerCards.keySet().retainAll(Collections.singleton(clickedUpgrade));

        PopupManager.I().spawnPercentage(
            ((StatUpgrade) clickedUpgrade).getAdditionalPercentage(),
            Assets.I().colorGreen(),
            cardBounds.get(clickedUpgrade).getX() + 1f,
            cardBounds.get(clickedUpgrade).getY()).setOnFinished(() -> onUpgradeClicked.run());
        clickedUpgrade.apply();
    }

    @Override
    protected TextureRegion getTexture(Upgrade upgrade) {
        return new TextureRegion(((StatUpgrade) upgrade).getStat().getTexture());
    }

    @Override
    protected TextureRegion getShadow(Upgrade upgrade) {
        return new TextureRegion(((StatUpgrade) upgrade).getStat().getShadowTexture());
    }
}
