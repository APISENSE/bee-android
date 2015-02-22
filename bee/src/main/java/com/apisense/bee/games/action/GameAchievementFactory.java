package com.apisense.bee.games.action;

import com.apisense.bee.games.action.share.ShareAceAchievement;
import com.apisense.bee.games.action.signin.GoogleSignInAchievement;
import com.apisense.bee.games.action.subscribe.CrowdSensingAceAchievement;
import com.apisense.bee.games.action.subscribe.CrowdSensingPartnerAchievement;
import com.apisense.bee.games.action.subscribe.CrowdSensingSpecialistAchievement;
import com.apisense.bee.games.action.subscribe.FirstMissionAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * Created by Warnant on 19-02-15.
 */
public class GameAchievementFactory {

    public static GameAchievement getGameAchievement(Achievement achievement) {
        switch (achievement.getAchievementId()) {
            case GameAchievement.FIRST_MISSION_KEY:
                return new FirstMissionAchievement(achievement);
            case GameAchievement.SHARE_ACE_KEY:
                return new ShareAceAchievement(achievement);
            case GameAchievement.GOOGLE_SIGN_IN_KEY:
                return new GoogleSignInAchievement(achievement);
            case GameAchievement.CROWD_SENSING_ACE_KEY:
                return new CrowdSensingAceAchievement(achievement);
            case GameAchievement.CROWD_SENSING_PARTNER_KEY:
                return new CrowdSensingPartnerAchievement(achievement);
            case GameAchievement.CROWD_SENSING_SPECIALIST_KEY:
                return new CrowdSensingSpecialistAchievement(achievement);
            default:
                throw new IllegalStateException();
        }
    }

}
