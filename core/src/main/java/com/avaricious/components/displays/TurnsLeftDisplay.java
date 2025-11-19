package com.avaricious.components.displays;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TurnsLeftDisplay {

    private final Texture turnsLeftTexture;

    private final Texture[] appliesLeft = new Texture[2];
    private final Texture[] spinsLeft = new Texture[2];

    private float hoverTime = 0f;

    public TurnsLeftDisplay() {
        turnsLeftTexture = Assets.I().getButtonsLeftDisplay();
        for(int i = 0; i < 2; i++) {
            appliesLeft[i] = Assets.I().unlitNumber();
            spinsLeft[i] = Assets.I().unlitNumber();
        }
    }

    public void draw(SpriteBatch batch, float delta) {
        hoverTime += delta;
        float hoverOffset = (float) Math.sin(hoverTime * 1.5f/*hoverTime*/) * 0.03f/*hoverStrength*/;

        float baseY = 2.275f + hoverOffset;
        float applyNumberBaseY = 3.915f + hoverOffset;
        float spinNumberBaseY = 2.915f + hoverOffset;

        batch.draw(turnsLeftTexture, 0.75f, baseY, 3.84f, 2.6f);
        batch.setColor(Assets.I().lightColor());
        for(int i = 0; i < 2; i++) {
            batch.draw(appliesLeft[i], 3.475f + (i * 0.35f), applyNumberBaseY, 8 / 30f, 14 / 30f);
            batch.draw(spinsLeft[i], 3.475f + (i * 0.35f), spinNumberBaseY, 8 / 30f, 14 / 30f);
        }
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void setAppliesLeft(int amount) {
        appliesLeft[1] = Assets.I().getDigitalNumber(amount % 10);
        appliesLeft[0] = Assets.I().getDigitalNumber((amount / 10) % 10);
    }

    public void setSpinsLeft(int amount) {
        spinsLeft[1] = Assets.I().getDigitalNumber(amount % 10);
        spinsLeft[0] = Assets.I().getDigitalNumber((amount / 10) % 10);
    }

}
