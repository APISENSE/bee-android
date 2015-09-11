package com.apisense.bee.games.action.achievement;

import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * Is achieved when the user goes to the Privacy settings activity for the first time.
 */
public class LookedAtPrivacySettingsAchievement extends GameAchievement {

    public LookedAtPrivacySettingsAchievement(Achievement achievement) {
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
