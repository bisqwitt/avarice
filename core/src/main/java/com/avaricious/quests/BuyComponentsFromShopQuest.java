package com.avaricious.quests;

public class BuyComponentsFromShopQuest extends Quest {

    private int componentsBought = 0;

    public void componentsBought(){
        componentsBought++;
        if(componentsBought == 5) completed();
    }

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
        return "Buy 5 Components in the Shop (" + componentsBought + "/5)";
    }
}
