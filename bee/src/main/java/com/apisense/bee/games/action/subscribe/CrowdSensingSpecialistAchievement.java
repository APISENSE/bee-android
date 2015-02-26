package com.apisense.bee.games.action.subscribe;

import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

import fr.inria.asl.utils.Log;

/**
 * Created by Warnant on 19-02-15.
 */
public class CrowdSensingSpecialistAchievement extends GameAchievement implements MissionSuscribeAchievement {

    public static final int NUMBER_MISSION_REQUIRED = 6;

    public CrowdSensingSpecialistAchievement(Achievement achievement) {
        super(achievement);
    }

    @Override
    public boolean process() {
        Log.getInstance().i("CrowdSensingSpecialistAchievement", "size=" + BeeGameManager.getInstance().getCurrentExperiments().size());
        //TODO category field in experiment needed
        return BeeGameManager.getInstance().getCurrentExperiments().size() >= NUMBER_MISSION_REQUIRED;

    }

    @Override
    public int getScore() {
        return 1;
    }

    @Override
    public long getPoints() {
        return 10 * super.getPoints();
    }
}
