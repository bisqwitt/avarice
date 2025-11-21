package com.avaricious.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class PopupManager {
    private Array<NumberPopup> popups = new Array<>();

    public void spawn(Texture numberTexture, Color color, float x, float y) {
        popups.add(new NumberPopup(numberTexture, color, x, y));
    }

    public void update(float delta) {
        for (int i = popups.size - 1; i >= 0; i--) {
            NumberPopup p = popups.get(i);
            p.update(delta);
            if (p.isFinished()) {
                popups.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (NumberPopup p : popups) {
            p.render(batch);
        }
    }
}

