package com.apisense.bee.games.action;

/**
 * Created by Warnant on 12-02-15.
 */
public class FirstMissionGameAchievement extends GameAchievement {

    private static final String FIRST_MISSION_GPG_ID_KEY = "CgkIl-DToIgLEAIQAg";

    public FirstMissionGameAchievement() {
        super(FIRST_MISSION_GPG_ID_KEY, false);
        this.name = this.getClass().getName();
    }

    @Override
    public boolean perform() {
        return false;
    }
}
