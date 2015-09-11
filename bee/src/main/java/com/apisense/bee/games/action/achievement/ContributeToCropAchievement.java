package com.apisense.bee.games.action.achievement;

import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * Is achieved when a user subscribed to a certain amount of crops.
 */
public class ContributeToCropAchievement extends GameAchievement {
    private final int cropLimit;

    public ContributeToCropAchievement(Achievement achievement, int amountOfCrops) {
        super(achievement);
        this.cropLimit = amountOfCrops;
    }

    @Override
    public boolean process() {
        return BeeGameManager.getInstance().getCurrentExperiments().size() >= cropLimit;
    }

    @Override
    public int getScore() {
        return 0;
    }
}
