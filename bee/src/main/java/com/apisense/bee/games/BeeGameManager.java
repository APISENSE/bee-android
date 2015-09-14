package com.apisense.bee.games;


import android.content.Intent;
import android.util.Log;

import com.apisense.bee.games.utils.BaseGameActivity;
import com.apisense.bee.games.utils.GameHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.Achievements;

import java.util.ArrayList;
import java.util.List;

/**
 * This singleton class is used to handle all game actions and interaction inside the app.
 * The class manages all events coming from the app and loads the game content.
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public class BeeGameManager implements GameManagerInterface {
    private static final String TAG = "BeeGameManager";
    private static BeeGameManager instance;

    /**
     * The current achievement map data indexed by the Play Games ID
     */
    private List<Achievement> currentAchievements;

    /**
     * @see com.apisense.bee.games.utils.GameHelper
     */
    private GameHelper gh;

    /**
     * Default constructor
     */
    private BeeGameManager() {
        this.currentAchievements = new ArrayList<>();
    }

    /**
     * This method returns the current instance of the manager.
     *
     * @return BeeGameManager the instance
     */
    public static BeeGameManager getInstance() {
        if (instance == null) {
            instance = new BeeGameManager();
        }
        return instance;
    }

    /**
     * This method returns the Google API Client attached to the current game manager
     *
     * @return GoogleApiClient the current client
     * @see com.apisense.bee.games.utils.GameHelper
     */
    public GoogleApiClient getGoogleApiClient() {
        return this.gh.getApiClient();
    }

    /**
     * This method returns the count of finished achievements on the game
     *
     * @return int the current count of finished achievements
     */
    public int getUnlockedAchievementsCount() {
        int count = 0;
        for (Achievement achievement : currentAchievements) {
            if (achievement.getState() == Achievement.STATE_UNLOCKED) {
                count++;
            }
        }
        return count;
    }

    /**
     * This method is used to connect the current Game Helper to Play Games
     *
     * @see com.apisense.bee.games.utils.GameHelper
     */
    public void connectPlayer() {
        this.gh.connect();
    }

    /**
     * @see com.apisense.bee.games.GameManagerInterface
     */
    @Override
    public void initialize(BaseGameActivity currentActivity) {
        if (currentActivity == null) {
            return;
        }
        gh = new GameHelper(currentActivity, GameHelper.CLIENT_ALL);
        gh.setup(currentActivity);
        gh.enableDebugLog(true);
        gh.setConnectOnStart(false);
        currentActivity.setGameHelper(gh);

//        Log.i(TAG, "Loading player data ... : " + this.refreshPlayerData());
    }

    /**
     * @see com.apisense.bee.games.GameManagerInterface
     */
    @Override
    public boolean refreshPlayerData() {
        if (gh.isSignedIn()) {
            // load achievements
            Games.Achievements.load(gh.getApiClient(), true)
                    .setResultCallback(new ResultCallback<Achievements.LoadAchievementsResult>() {
                        @Override
                        public void onResult(Achievements.LoadAchievementsResult loadAchievementsResult) {
                            Log.i(TAG, "Handle method onResult for refreshPlayerData");
                            currentAchievements.clear();
                            DataBuffer<Achievement> achievementBuffer = loadAchievementsResult.getAchievements();
                            for (Achievement achievement : achievementBuffer) {
                                Log.i(TAG, "Achievement=" + achievement.getName() + "&status=" + achievement.getState());
                                currentAchievements.add(achievement);
                            }
                        }
                    });
            return true;
        }
        return false;
    }

    /**
     * @see com.apisense.bee.games.GameManagerInterface
     */
    @Override
    public Intent getAchievementListIntent() {
        return Games.Achievements.getAchievementsIntent(gh.getApiClient());
    }

    /**
     * @see com.apisense.bee.games.GameManagerInterface
     */
    @Override
    public boolean alreadySignedIn() {
        return gh.isSignedIn();
    }
}
