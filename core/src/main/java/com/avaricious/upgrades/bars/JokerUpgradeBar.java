package com.avaricious.upgrades.bars;

import com.avaricious.Assets;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

public class JokerUpgradeBar extends UpgradeBar {

    private final TextureRegion jokerCard;
    private final TextureRegion jokerCardShadow;

    public JokerUpgradeBar(List<? extends Upgrade> upgrades, Rectangle cardRectangle, float offset, boolean tooltipOnTop) {
        super(upgrades, cardRectangle, offset, tooltipOnTop);

        jokerCard = new TextureRegion(Assets.I().getJokerCard());
        jokerCardShadow = new TextureRegion(Assets.I().getJokerCardShadow());
    }

    @Override
    protected void onCardClicked(Upgrade clickedUpgrade) {
        UpgradesManager.I().addUpgrade(clickedUpgrade);
        cardBounds.remove(clickedUpgrade);
        cardAnimationManagers.remove(clickedUpgrade);
    }

    @Override
    protected TextureRegion getTexture(Upgrade upgrade) {
        return jokerCard;
    }

    @Override
    protected TextureRegion getShadow(Upgrade upgrade) {
        return jokerCardShadow;
    }
}
