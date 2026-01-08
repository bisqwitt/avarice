package com.avaricious;

import com.badlogic.gdx.math.Rectangle;

public class CreditScore extends CreditNumber {

    private final AutoCloseable creditListener;

    public CreditScore(int initialScore, Rectangle rectangle, float offset) {
        super(initialScore, rectangle, offset);

        creditListener = CreditManager.I().onCreditChange(this::setScore);
    }

    public void dispose() {
        try {
            creditListener.close();
        } catch (Exception ignore) {
        }
    }
}
