package com.avaricious.quests;

public class QuestManager {

    private static QuestManager instance;
    public static QuestManager I(){
        return instance == null ? instance = new QuestManager() : instance;
    }
    private QuestManager(){}

}
