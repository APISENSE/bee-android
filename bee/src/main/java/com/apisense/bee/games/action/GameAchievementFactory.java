package com.apisense.bee.games.action;

import com.google.android.gms.games.achievement.Achievement;

/**
 * Created by Warnant on 19-02-15.
 */
public class GameAchievementFactory {

    public static GameAchievement getGameAchievement(Achievement achievement) {
        switch (achievement.getAchievementId()) {
            case GameAchievement.FIRST_MISSION_GPG_KEY:
                return new FirstMissionAchievement(achievement);
            case GameAchievement.SHARE_ACE_GPG_KEY:
                return new ShareAceAchievement(achievement);
            case GameAchievement.SIGN_IN_GPG_KEY:
                return new SignInAchievement(achievement);
            case GameAchievement.CROWD_SENSING_GPG_KEY:
                return new CrowdSensingMissionAchievement(achievement);
            //TODO other achievement
            default:
                throw new IllegalStateException();
        }
    }

}
