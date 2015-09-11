package com.apisense.bee.games.action.achievement;

import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * This achievement is unlocked when the user sign in on the Google play Game.
 * Always successful when using GPG.
 *
 */
public class GooglePlayGamesSignInAchievement extends GameAchievement {

    /**
     * Constructor
     *
     * @param achievement Achievement the official achievement object
     */
    public GooglePlayGamesSignInAchievement(Achievement achievement) {
        super(achievement);
    }

    @Override
    public boolean process() {
        return true;
    }

    @Override
    public int getScore() {
        return 1;
    }
}
