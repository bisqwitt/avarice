package com.avaricious.quests;

import com.avaricious.CreditManager;

public abstract class Quest {

    public abstract int getPrice();
    public abstract int getReward();
    public abstract String getDescription();

    public void completed() {
        CreditManager.I().gain(getReward());
    }
}
