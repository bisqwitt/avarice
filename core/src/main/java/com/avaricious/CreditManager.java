package com.avaricious;

import com.badlogic.gdx.Gdx;

public class CreditManager {

    private static CreditManager instance;

    public static CreditManager I() {
        return instance == null ? (instance = new CreditManager()) : instance;
    }

    private CreditManager() {
        credits = 5;
    }

    private int credits;

    public void onRoundBeaten(int handsLeft) {
        credits += (credits/5) + handsLeft + 3;
    }

    public void gain(int amount) {
        credits += amount;
    }

    public void pay(int amount) {
        credits -= amount;
    }

    public int getCredits() {
        return credits;
    }

    public boolean payIfEnough(int amount) {
        if(credits >= amount) {
            pay(amount);
            return true;
        }
        return false;
    }
}
