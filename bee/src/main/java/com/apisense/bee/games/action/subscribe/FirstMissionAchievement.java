package com.apisense.bee.games.action.subscribe;

import android.util.Log;

import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * This class represents the methods of a specialized achievement of first mission
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public class FirstMissionAchievement extends GameAchievement implements MissionSubscribeAchievement {

    public static final String TAG = "BeeFirstMission";

    /**
     * Constructor
     *
     * @param achievement Achievement the official achievement object
     */
    public FirstMissionAchievement(Achievement achievement) {
        super(achievement);
    }

    /**
     * @see com.apisense.bee.games.action.GameAchievement
     */
    @Override
    public boolean process() {
        Log.i(TAG, "size=" + BeeGameManager.getInstance().getCurrentExperiments().size());
        return BeeGameManager.getInstance().getCurrentExperiments().size() >= 1;
    }

    /**
     * @see com.apisense.bee.games.action.GameAchievement
     */
    @Override
    public int getScore() {
        return 1;
    }
}
