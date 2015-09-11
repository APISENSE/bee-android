package com.apisense.bee.games.action;

import android.util.Log;

import com.apisense.bee.games.action.share.ShareAceAchievement;
import com.apisense.bee.games.action.signin.FacebookSignInAchievement;
import com.apisense.bee.games.action.signin.GoogleSignInAchievement;
import com.apisense.bee.games.action.subscribe.CrowdSensingAceAchievement;
import com.apisense.bee.games.action.subscribe.CrowdSensingPartnerAchievement;
import com.apisense.bee.games.action.subscribe.CrowdSensingSpecialistAchievement;
import com.apisense.bee.games.action.subscribe.FirstMissionAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * This class creates the custom achievement objects from the official achievement object used by the application
 *
 * @author Quentin Warnant
 * @version 1.0
 * @see com.apisense.bee.games.action.GameAchievement
 */
public class GameAchievementFactory {
    private static final String TAG = "GameAchievementFactory";

    /**
     * This method creates the custom achievement object from the official achievement object
     *
     * @param achievement Achievement the official achievement object of the Play Games library
     * @return GameAchievement the custom achievement object
     */
    public static GameAchievement getGameAchievement(Achievement achievement) {
        Log.i(TAG, achievement.getAchievementId() + "" + achievement.getName());
        switch (achievement.getAchievementId()) {
            case GameAchievement.CURIOUS_KEY:
                return new FirstMissionAchievement(achievement);
            case GameAchievement.SECRETIVE_KEY:
                return new ShareAceAchievement(achievement);
            case GameAchievement.NEW_BEE_KEY:
                return new GoogleSignInAchievement(achievement);
            case GameAchievement.JOIN_SWARM_KEY:
                return new FacebookSignInAchievement(achievement);
            case GameAchievement.RECRUITING_KEY:
                return new CrowdSensingAceAchievement(achievement);
            case GameAchievement.CHATTY_KEY:
                return new CrowdSensingPartnerAchievement(achievement);
            case GameAchievement.QUEEN_KEY:
                return new CrowdSensingSpecialistAchievement(achievement);
            default:
                Log.w(TAG, "Unknown achievement: "+ achievement.getAchievementId());
                return null;
        }
    }

}
