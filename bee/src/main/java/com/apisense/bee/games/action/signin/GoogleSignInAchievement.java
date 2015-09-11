package com.apisense.bee.games.action.signin;

import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * This class represents the methods of a specialized achievement of Facebook Sign-In
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public class GoogleSignInAchievement extends GameAchievement {

    /**
     * Constructor
     *
     * @param achievement Achievement the official achievement object
     */
    public GoogleSignInAchievement(Achievement achievement) {
        super(achievement);
    }

    @Override
    public boolean process() {
        //TODO Google signin integration needed
        return true;
    }

    @Override
    public int getScore() {
        return 1;
    }
}
