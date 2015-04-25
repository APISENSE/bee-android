package com.apisense.bee.games.action.signin;

import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * This class represents the methods of a specialized achievement of Facebook Sign-In
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public class FacebookSignInAchievement extends GameAchievement implements SignInAchievement {

    /**
     * Constructor
     *
     * @param achievement Achievement the official achievement object
     */
    public FacebookSignInAchievement(Achievement achievement) {
        super(achievement);
    }

    @Override
    public boolean process() {
        //TODO Facebook signin integration needed
        return false;
    }

    @Override
    public int getScore() {
        return 1;
    }
}