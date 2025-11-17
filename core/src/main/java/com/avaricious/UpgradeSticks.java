package com.avaricious;

import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradeRarity;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeSticks {

    private final Map<UpgradeRarity, Texture> textures = Map.of(
        UpgradeRarity.COMMON, Assets.I().getCommonStick(),
        UpgradeRarity.UNCOMMON, Assets.I().getUncommonStick(),
        UpgradeRarity.RARE, Assets.I().getRareStick(),
        UpgradeRarity.EPIC, Assets.I().getEpicStick(),
        UpgradeRarity.LEGENDARY, Assets.I().getLegendaryStick()
    );
    private int hoverIndex = -1;

    // animated x-offset per item (meters). Grows/shrinks toward target (0.5f or 0f).
    private float[] xOffsets = new float[0];

    // how quickly offsets approach target (per second). Tweak to taste.
    private static final float APPROACH_SPEED = 10f;

    public UpgradeSticks() {
    }

    public void draw(SpriteBatch batch) {
        List<Upgrade> upgrades = UpgradesManager.I().getUpgrades();
        ensureCapacity(upgrades.size());

        float dt = Gdx.graphics.getDeltaTime();
        // convert a per-second speed to a frame lerp factor (exponential smoothing)
        float alpha = 1f - (float)Math.pow(1f - Math.min(APPROACH_SPEED * dt, 1f), 1f);

        for (int i = 0; i < upgrades.size(); i++) {
            float baseX = 14.475f;
            float target = (hoverIndex == i) ? 0.5f : 0f;

            // ease the offset toward target
            xOffsets[i] = MathUtils.lerp(xOffsets[i], target, alpha);

            float x = baseX + xOffsets[i];
            float y = 7.21f - (i * 0.76f);

            batch.draw(textures.get(upgrades.get(i).getRarity()), x, y, 1.1818f, 0.6815f);
        }
    }

    public void hoveringAt(Vector3 mouse) {
        List<Upgrade> upgrades = UpgradesManager.I().getUpgrades();
        ensureCapacity(upgrades.size());

        hoverIndex = -1;
        for (int i = 0; i < upgrades.size(); i++) {
            float x = 14.475f;
            float y = 7.21f - (i * 0.76f);
            if (new Rectangle(x, y, 1.1818f, 0.6815f).contains(mouse.x, mouse.y)) {
                hoverIndex = i;
                break;
            }
        }
    }

    private void ensureCapacity(int size) {
        if (xOffsets.length != size) {
            float[] newArr = new float[size];
            int copy = Math.min(xOffsets.length, size);
            if (copy > 0) System.arraycopy(xOffsets, 0, newArr, 0, copy);
            xOffsets = newArr;
        }
    }
}
