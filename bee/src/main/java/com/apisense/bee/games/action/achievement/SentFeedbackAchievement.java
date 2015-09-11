package com.apisense.bee.games.action.achievement;

import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * Is achieved when the user sent us feedback at least once.
 */
public class SentFeedbackAchievement extends GameAchievement {

    public SentFeedbackAchievement(Achievement achievement) {
        super(achievement);
    }

    @Override
    public boolean process() {
        return false;
    }

    @Override
    public int getScore() {
        return 0;
    }
}
