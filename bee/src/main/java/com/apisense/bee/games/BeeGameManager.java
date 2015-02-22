package com.apisense.bee.games;


import android.content.Intent;

import com.apisense.bee.games.action.GameAchievement;
import com.apisense.bee.games.action.GameAchievementFactory;
import com.apisense.bee.games.action.share.ShareAceAchievement;
import com.apisense.bee.games.action.subscribe.MissionSuscribeAchievement;
import com.apisense.bee.games.event.GameEvent;
import com.apisense.bee.games.event.GameEventListener;
import com.apisense.bee.games.event.MissionSubscribeEvent;
import com.apisense.bee.games.event.ShareEvent;
import com.apisense.bee.games.utils.BaseGameActivity;
import com.apisense.bee.games.utils.GameHelper;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.Achievements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.asl.utils.Log;
import fr.inria.bsense.appmodel.Experiment;

public class BeeGameManager implements GameManagerInterface, GameEventListener {

    public static final String MISSIONS_LEADERBOARD_ID = "CgkIl-DToIgLEAIQBA";
    private static BeeGameManager instance;

    private BaseGameActivity currentActivity;
    private Map<String, GameAchievement> currentAchievements;
    private List<Experiment> currentExperiments;


    private BeeGameManager() {
        this.currentActivity = null;
        this.currentAchievements = new HashMap<>();
        this.currentExperiments = new ArrayList<>();
    }

    public static BeeGameManager getInstance() {
        if (instance == null) {
            instance = new BeeGameManager();
        }
        return instance;
    }

    private List<GameAchievement> getGameAchievements(GameEvent event) {
        List<GameAchievement> achievements = new ArrayList<>();
        for (GameAchievement ga : currentAchievements.values()) {
            if ((ga instanceof MissionSuscribeAchievement && event instanceof MissionSubscribeEvent) ||
                    (ga instanceof ShareAceAchievement && event instanceof ShareEvent)) {
                achievements.add(ga);
            }
        }
        return achievements;
    }

    private void refreshExperiments(GameEvent gameEvent) {
        if (gameEvent instanceof MissionSubscribeEvent) {
            List<Experiment> lastExperiments = ((MissionSubscribeEvent) gameEvent).getExperiments();
            if (lastExperiments != null) {
                this.currentExperiments = lastExperiments;
            }
        }
    }

    public List<Experiment> getCurrentExperiments() {
        return this.currentExperiments;
    }

    @Override
    public void fireGameEventPerformed(GameEvent gameEvent) {
        Log.getInstance().i("BeeGameManager : FireGameEventPerformed : " + gameEvent);

        refreshExperiments(gameEvent);

        for (GameAchievement gameAchievement : getGameAchievements(gameEvent)) {

            if (gameAchievement.process()) {
                this.pushAchievement(gameAchievement);
            }

        }
    }

    @Override
    public void initialize(BaseGameActivity currentActivity) {
        this.currentActivity = currentActivity;

        GameHelper gh = new GameHelper(this.currentActivity, GameHelper.CLIENT_GAMES);
        gh.setup(this.currentActivity);
        gh.enableDebugLog(true);
        gh.setConnectOnStart(true);
        this.currentActivity.setGameHelper(gh);

        Log.getInstance().i("BeeGameManager : Loading player data ... : " + this.refreshPlayerData());
    }

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
                        //TODO add the leadboard of the achievement in the object for push score

                        Log.getInstance().i("BeeGameManager : Achievement=" + achievement.getName() + "&status=" + achievement.getState());
                    }

                    Log.getInstance().i("BeeGameManager : Handle method onResult for refreshPlayerData");

                }
            });
            return true;
        }
        return false;
    }


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

        Log.getInstance().i("BeeGameManager : GPG Push Achievement : " + gameAchievement);

        // Push the score if needed
        this.pushScore(gameAchievement.getLeadboard(), gameAchievement.getScore());
    }

    @Override
    public GameAchievement getAchievement(String achievementId) {
        return this.currentAchievements.get(achievementId);
    }

    @Override
    public Intent getAchievementList() {
        return Games.Achievements.getAchievementsIntent(this.currentActivity.getApiClient());
    }

    @Override
    public void pushScore(String leardboardId, int score) {
        if (!isConnected()) {
            return;
        }

        if (leardboardId == null || score <= 0) {
            return;
        }

        //TODO check if this incremental of if we must make the computation
        Games.Leaderboards.submitScore(this.currentActivity.getApiClient(), leardboardId, score);

        Log.getInstance().i("BeeGameManager : GPG Push Score for leaderboard " + leardboardId + " with score + " + score);
    }

    @Override
    public Intent getLeaderboard(String leaderboardId) {
        return Games.Leaderboards.getLeaderboardIntent(this.currentActivity.getApiClient(),
                leaderboardId);
    }

    @Override
    public boolean isConnected() {
        return this.currentActivity.getApiClient().isConnected();

    }

    public int getAchievementUnlockCount() {
        int count = 0;
        for (GameAchievement achievement : currentAchievements.values()) {
            if (achievement.isFinished()) {
                count++;
            }
        }
        return count;
    }

    public int getAchievementLockCount() {
        int count = 0;
        for (GameAchievement achievement : currentAchievements.values()) {
            if (!achievement.isFinished()) {
                count++;
            }
        }
        return count;
    }

}
