package com.apisense.bee.games;


import android.content.Intent;
import android.util.Log;

import com.apisense.bee.games.action.GameAchievement;
import com.apisense.bee.games.action.GameAchievementFactory;
import com.apisense.bee.games.action.share.ShareAceAchievement;
import com.apisense.bee.games.action.subscribe.MissionSubscribeAchievement;
import com.apisense.bee.games.event.GameEvent;
import com.apisense.bee.games.event.GameEventListener;
import com.apisense.bee.games.event.MissionSubscribeEvent;
import com.apisense.bee.games.event.OnGameDataLoadedEvent;
import com.apisense.bee.games.event.OnGameDataLoadedListener;
import com.apisense.bee.games.event.ShareEvent;
import com.apisense.bee.games.utils.BaseGameActivity;
import com.apisense.bee.games.utils.GameHelper;
import com.apisense.sdk.core.store.Crop;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.Achievements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This singleton class is used to handle all game actions and interaction inside the app.
 * The class manages all events coming from the app and loads the game content.
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public class BeeGameManager implements GameManagerInterface, GameEventListener {
    private static final String TAG = "BeeGameManager";
    /**
     * The ID of the mission leaderboard on the Play Games
     */
    public static final String MISSIONS_LEADERBOARD_ID = "CgkIl-DToIgLEAIQBA";
    private static BeeGameManager instance;
    /**
     * The current activity attached to the Game Manager
     */
    private BaseGameActivity currentActivity;

    /**
     * The listener list of the game data change
     */
    private List<OnGameDataLoadedListener> gameDataLoadedListeners;

    /**
     * The current achievement map data indexed by the Play Games ID
     */
    private Map<String, GameAchievement> currentAchievements;

    /**
     * The current Bee experiment list from the APISENSE server
     */
    private List<Crop> currentExperiments;

    /**
     * @see com.apisense.bee.games.utils.GameHelper
     */
    private GameHelper gh;

    /**
     * Default constructor
     */
    private BeeGameManager() {
        this.currentActivity = null;
        this.currentAchievements = new HashMap<>();
        this.currentExperiments = new ArrayList<>();
        this.gameDataLoadedListeners = new ArrayList<>();
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
     * This method returns the current player points for the Google Play Games account
     * with all achievements done
     *
     * @return long the point amount
     */
    public long getPlayerPoints() {
        long points = 0;

        for (GameAchievement a : currentAchievements.values()) {
            if (a.isFinished()) {
                points += a.getPoints();
                Log.i("BeePoints", a.toString());
            }
        }

        return points;
    }

    /**
     * This method returns the list of the interested achievements depending on the game event type
     *
     * @param event GameEvent the incoming game event
     * @return List the list of the interested achievements
     */
    private List<GameAchievement> getGameAchievements(GameEvent event) {
        List<GameAchievement> achievements = new ArrayList<>();
        for (GameAchievement ga : currentAchievements.values()) {
            if ((ga instanceof MissionSubscribeAchievement && event instanceof MissionSubscribeEvent) ||
                    (ga instanceof ShareAceAchievement && event instanceof ShareEvent)) {
                achievements.add(ga);
            }
        }
        return achievements;
    }

    /**
     * This method is used to set the current list of Bee experiment inside the manager.
     * The interest of this list is that the manager doesn't have to reload the list each time
     * to perform some actions on the list.
     *
     * @param experiments List<Experiment> the new experiment list
     */
    public void setExperiments(List<Crop> experiments) {
        this.currentExperiments = experiments;
    }

    /**
     * This method returns the current list of Bee experiments
     *
     * @return List<Experiment> the current list
     */
    public List<Crop> getCurrentExperiments() {
        return this.currentExperiments;
    }

    /**
     * This method is used to add a game data loaded listener on the manager
     *
     * @param listener OnGameDataLoadedListener the new listener to add
     * @return boolean true if the listener has been added, false otherwise
     */
    public boolean addOnGameDataLoadedListener(OnGameDataLoadedListener listener) {
        return this.gameDataLoadedListeners.add(listener);
    }

    /**
     * This method is used to remove a game data loaded listener from the manager
     *
     * @param listener OnGameDataLoadedListener the listener to remove
     * @return boolean true if the listener has been removed, false otherwise
     */
    public boolean removeOnGameDataLoadedListener(OnGameDataLoadedListener listener) {
        return this.gameDataLoadedListeners.remove(listener);
    }

    /**
     * This method notifies all game data loaded listeners on the manager
     */
    private void notifyGameDataLoadedListeners() {
        for (OnGameDataLoadedListener listener : gameDataLoadedListeners) {
            listener.onRefresh(new OnGameDataLoadedEvent(currentActivity));
        }
    }

    /**
     * This method returns the state of the game manager
     *
     * @return boolean true if the game data from the Google Play Games are loaded, false otherwise
     */
    public boolean isLoad() {
        return this.currentAchievements.size() > 0;
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
    public int getAchievementUnlockCount() {
        int count = 0;
        for (GameAchievement achievement : currentAchievements.values()) {
            if (achievement.isFinished()) {
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

    @Override
    public void fireGameEventPerformed(GameEvent gameEvent) {
        Log.i(TAG, "FireGameEventPerformed : " + gameEvent);

        for (GameAchievement gameAchievement : getGameAchievements(gameEvent)) {

            boolean process = gameAchievement.process();
            Log.i(TAG, "FireGameEventPerformed : " + gameAchievement + " result=" + process);

            if (process) {
                this.pushAchievement(gameAchievement);
            }

        }
    }

    /**
     * @see com.apisense.bee.games.GameManagerInterface
     */
    @Override
    public void initialize(BaseGameActivity currentActivity) {
        if (currentActivity == null) {
            return;
        }
        this.currentActivity = currentActivity;

        gh = new GameHelper(this.currentActivity, GameHelper.CLIENT_ALL);
        gh.setup(this.currentActivity);
        gh.enableDebugLog(true);
        gh.setConnectOnStart(false);
        this.currentActivity.setGameHelper(gh);

        Log.i(TAG, "Loading player data ... : " + this.refreshPlayerData());
    }

    /**
     * @see com.apisense.bee.games.GameManagerInterface
     */
    @Override
    public boolean refreshPlayerData() {
        if (this.currentActivity != null) {
            // load achievements
            Games.Achievements.load(this.currentActivity.getApiClient(), true).setResultCallback(new ResultCallback<Achievements.LoadAchievementsResult>() {
                @Override
                public void onResult(Achievements.LoadAchievementsResult loadAchievementsResult) {
                    currentAchievements.clear();

                    for (Achievement achievement : loadAchievementsResult.getAchievements()) {

                        // Get the game achievement associated to the gpg achievement
                        GameAchievement gameAchievement = GameAchievementFactory.getGameAchievement(achievement);
                        // Put the achievement on the current list
                        currentAchievements.put(achievement.getAchievementId(), gameAchievement);

                        Log.i(TAG, "Achievement=" + achievement.getName() + "&status=" + achievement.getState());
                    }

                    notifyGameDataLoadedListeners();
                    Log.i(TAG, "Handle method onResult for refreshPlayerData");
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
    public void pushAchievement(GameAchievement gameAchievement) {
        if (!isConnected())
            return;

        // Check if the achievement is not already finished
        if (currentAchievements.get(gameAchievement.getId()).isFinished()) {
            return;
        }

        // Check if the achievement is incremental
        if (gameAchievement.isIncremental()) {
            Games.Achievements.increment(this.currentActivity.getApiClient(), gameAchievement.getId(), gameAchievement.getCurrentSteps());
        } else {
            Games.Achievements.unlock(this.currentActivity.getApiClient(), gameAchievement.getId());
        }

        Log.i(TAG, "GPG Push Achievement : " + gameAchievement);

        // Push the score if needed
        this.pushScore(gameAchievement.getLeadboard(), gameAchievement.getScore());

        // Refresh player data
        this.refreshPlayerData();
    }

    /**
     * @see com.apisense.bee.games.GameManagerInterface
     */
    @Override
    public GameAchievement getAchievement(String achievementId) {
        return this.currentAchievements.get(achievementId);
    }

    /**
     * @see com.apisense.bee.games.GameManagerInterface
     */
    @Override
    public Intent getAchievementList() {
        return Games.Achievements.getAchievementsIntent(this.currentActivity.getApiClient());
    }

    /**
     * @see com.apisense.bee.games.GameManagerInterface
     */
    @Override
    public void pushScore(String leardboardId, int score) {
        if (!isConnected()) {
            return;
        }

        if (leardboardId == null || score <= 0) {
            return;
        }

        Games.Leaderboards.submitScore(this.currentActivity.getApiClient(), leardboardId, score);

        Log.i(TAG, "GPG Push Score for leaderboard " + leardboardId + " with score + " + score);
    }

    /**
     * @see com.apisense.bee.games.GameManagerInterface
     */
    @Override
    public Intent getLeaderboard(String leaderboardId) {
        return Games.Leaderboards.getLeaderboardIntent(this.currentActivity.getApiClient(),
                leaderboardId);
    }

    /**
     * @see com.apisense.bee.games.GameManagerInterface
     */
    @Override
    public boolean isConnected() {
        return this.getGoogleApiClient().isConnected();

    }

}
