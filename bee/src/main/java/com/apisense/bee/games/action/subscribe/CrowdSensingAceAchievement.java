package com.apisense.bee.games.action.subscribe;

import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * Created by Warnant on 19-02-15.
 */
public class CrowdSensingAceAchievement extends GameAchievement implements MissionSuscribeAchievement {

    public static final int NUMBER_MISSION_REQUIRED = 5;

    public CrowdSensingAceAchievement(Achievement achievement) {
        super(achievement);
    }

    @Override
    public boolean process() {
        return BeeGameManager.getInstance().getCurrentExperiments().size() >= NUMBER_MISSION_REQUIRED;
    }

    @Override
    public int getScore() {
        return 1;
    }
}
