package com.avaricious;

import com.avaricious.components.progressbar.ProgressBar;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.stream.IntStream;

public class XpBar extends ProgressBar {

    private final float STEP_WIDTH = 3 / 22f;
    private final float STEP_HEIGHT = 2 / 22f;

    private final float X = 14f;
    private final float Y = 2f;
    private final float OFFSET = 0.04325f;

    private final Texture xpOrb = Assets.I().getXpOrb();
    private final Runnable onLevelUp;

    private float xp;
    private int level = 1;
    private int furthestLitCellIndex = 0;

    public XpBar(Runnable onLevelUp) {
        super(100, Assets.I().getXpPixel());
        setMaxValue(xpPerLevel(level));

        this.onLevelUp = onLevelUp;
    }

    @Override
    public void draw(SpriteBatch batch) {
        float diff = Math.abs(xp - getDisplayedValue());
        setDisplayedValue(diff < 1 ? xp : xp > getDisplayedValue()
            ? getDisplayedValue() + diff / 30 : getDisplayedValue() - diff / 30);

        for(int i = 0; i < progress.length; i++) {
            batch.draw(progress[i], X, Y + (i * OFFSET), STEP_WIDTH, STEP_HEIGHT);
        }
//        batch.draw(border, 14.8f, 3.7f, 14 / 70f, 310 / 70f);
        batch.draw(xpOrb, X - 0.175f, Y - 0.6f, 25 / 52f, 25 / 52f);
    }

    @Override
    protected void updateProgressTextures() {
        super.updateProgressTextures();
        int furthestLitCellIndex = IntStream.range(0, progress.length)
            .filter(i -> progress[i] == lit)
            .max()
            .orElse(-1);

        if(this.furthestLitCellIndex != furthestLitCellIndex) {
            ParticleManager.I().create(X, Y + furthestLitCellIndex * OFFSET, ParticleType.XP);
            this.furthestLitCellIndex = furthestLitCellIndex;
        }
    }

    public void addXp(int amount) {
        xp += amount;
        if(xp > getMaxValue()) levelUp();
    }

    private void levelUp() {
        xp = 0;
        level += 1;
        setMaxValue(xpPerLevel(level));

        onLevelUp.run();
    }

    private int xpPerLevel(int level) {
        return 100 * level;
    }

}
