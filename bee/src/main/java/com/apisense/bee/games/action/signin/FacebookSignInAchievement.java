package com.apisense.bee.games.action.signin;

import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * Created by Warnant on 22-02-15.
 */
public class FacebookSignInAchievement extends GameAchievement implements SignInAchievement {

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