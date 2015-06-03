package com.apisense.bee.games.action.subscribe;

import android.util.Log;

import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * This class represents the methods of a specialized achievement of sensing ace
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public class CrowdSensingAceAchievement extends GameAchievement implements MissionSubscribeAchievement {
    public static final String TAG = "A:CrowdSensingAce";

    public static final int NUMBER_MISSION_REQUIRED = 5;

    /**
     * Constructor
     *
     * @param achievement Achievement the official achievement object
     */
    public CrowdSensingAceAchievement(Achievement achievement) {
        super(achievement);
    }

    /**
     * @see com.apisense.bee.games.action.GameAchievement
     */
    @Override
    public boolean process() {
        Log.i(TAG, "size=" + BeeGameManager.getInstance().getCurrentExperiments().size());

        return BeeGameManager.getInstance().getCurrentExperiments().size() >= NUMBER_MISSION_REQUIRED;
    }

    /**
     * @see com.apisense.bee.games.action.GameAchievement
     */
    @Override
    public int getScore() {
        return 1;
    }

    /**
     * @see com.apisense.bee.games.action.GameAchievement
     */
    @Override
    public long getPoints() {
        return 10 * super.getPoints();
    }
}
