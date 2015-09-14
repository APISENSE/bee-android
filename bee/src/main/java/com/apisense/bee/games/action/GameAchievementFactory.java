package com.apisense.bee.games.action;

import android.util.Log;

import com.apisense.bee.games.action.achievement.ContributeToCropAchievement;
import com.apisense.bee.games.action.achievement.GooglePlayGamesSignInAchievement;
import com.apisense.bee.games.action.achievement.LookedAtPrivacySettingsAchievement;
import com.apisense.bee.games.action.achievement.LookedAtTheStoreAchievement;
import com.apisense.bee.games.action.achievement.SentFeedbackAchievement;
import com.apisense.bee.games.action.achievement.SharedTheApplicationAchievement;
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

    // TODO: Some kind of smelly code to refactor
    public static final String NEW_BEE_KEY = "CgkIl-DToIgLEAIQAQ";
    public static final String JOIN_SWARM_KEY = "CgkIl-DToIgLEAIQBg";
    public static final String CURIOUS_KEY = "CgkIl-DToIgLEAIQAg";
    public static final String SECRETIVE_KEY = "CgkIl-DToIgLEAIQAw";
    public static final String RECRUITING_KEY = "CgkIl-DToIgLEAIQBQ";
    public static final String CHATTY_KEY = "CgkIl-DToIgLEAIQBw";
    public static final String QUEEN_KEY = "CgkIl-DToIgLEAIQCA";

    public static final String BRONZE_WINGS_KEY = "CgkIl-DToIgLEAIQEw";
    public static final String SILVER_WINGS_KEY = "CgkIl-DToIgLEAIQFA";
    public static final String GOLD_WINGS_KEY = "CgkIl-DToIgLEAIQFQ";
    public static final String CRYSTAL_WINGS_KEY = "CgkIl-DToIgLEAIQFg";

    /**
     * This method creates the custom achievement object from the official achievement object
     *
     * @param achievement Achievement the official achievement object of the Play Games library
     * @return GameAchievement the custom achievement object
     */
    public static GameAchievement getGameAchievement(Achievement achievement) {
        Log.i(TAG, achievement.getAchievementId() + " " + achievement.getName());
        switch (achievement.getAchievementId()) {
            case NEW_BEE_KEY:
                return new GooglePlayGamesSignInAchievement(achievement);
            case JOIN_SWARM_KEY:
                return null; // Login with Facebook or Twitter account - Unachievable
            case CURIOUS_KEY:
                return new LookedAtTheStoreAchievement(achievement);
            case SECRETIVE_KEY:
                return new LookedAtPrivacySettingsAchievement(achievement);
            case RECRUITING_KEY:
                return new SharedTheApplicationAchievement(achievement);
            case CHATTY_KEY:
                return new SentFeedbackAchievement(achievement);
            case QUEEN_KEY:
                return null; // Unachievable
            case BRONZE_WINGS_KEY:
                return new ContributeToCropAchievement(achievement, 10);
            case SILVER_WINGS_KEY:
                return new ContributeToCropAchievement(achievement, 100);
            case GOLD_WINGS_KEY:
                return new ContributeToCropAchievement(achievement, 1000);
            case CRYSTAL_WINGS_KEY:
                return new ContributeToCropAchievement(achievement, 10000);
            default:
                Log.w(TAG, "Unknown achievement: " + achievement.getAchievementId());
                return null;
        }
    }

}
