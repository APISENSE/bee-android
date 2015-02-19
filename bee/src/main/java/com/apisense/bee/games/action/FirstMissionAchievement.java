package com.apisense.bee.games.action;

import com.apisense.bee.games.BeeGameManager;

/**
 * Created by Warnant on 12-02-15.
 */
public class FirstMissionAchievement extends GameAchievement {

    private static final String FIRST_MISSION_GPG_ID_KEY = "CgkIl-DToIgLEAIQAg";

    public FirstMissionAchievement() {
        super(BeeGameManager.getInstance().getAchievement(FIRST_MISSION_GPG_ID_KEY).getGpgAchievement());
    }

    @Override
    public int getScore() {
        return 1;
    }
}
