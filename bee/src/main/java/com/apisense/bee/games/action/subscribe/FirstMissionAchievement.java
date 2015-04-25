package com.apisense.bee.games.action.subscribe;

import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

import fr.inria.asl.utils.Log;

/**
 * This class represents the methods of a specialized achievement of first mission
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public class FirstMissionAchievement extends GameAchievement implements MissionSuscribeAchievement {

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
        Log.getInstance().i("BeeFirstMission", "size=" + BeeGameManager.getInstance().getCurrentExperiments().size());
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
