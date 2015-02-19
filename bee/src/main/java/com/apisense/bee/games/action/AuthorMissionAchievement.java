package com.apisense.bee.games.action;

import com.google.android.gms.games.achievement.Achievement;

/**
 * Created by Warnant on 19-02-15.
 */
public class AuthorMissionAchievement extends GameAchievement {

    public AuthorMissionAchievement(Achievement achievement) {
        super(achievement);
    }

    @Override
    public boolean process() {

        //TODO get experiment list and check count

        return false;
    }

    @Override
    public int getScore() {
        return 1;
    }
}
