package com.apisense.bee.games.action.signin;

import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * Created by Warnant on 19-02-15.
 */
public class GoogleSignInAchievement extends GameAchievement implements SignInAchievement {

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
