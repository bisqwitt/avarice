package com.avaricious.quests;

public class BuyTheWholeShopQuest extends Quest {
    @Override
    public int getPrice() {
        return 5;
    }

    @Override
    public int getReward() {
        return 15;
    }

    @Override
    public String getDescription() {
        return "Buy the Whole Shop";
    }
}
