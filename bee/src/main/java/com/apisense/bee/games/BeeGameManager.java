package com.apisense.bee.games;


import android.content.Intent;

import com.apisense.bee.games.action.GameAchievement;
import com.apisense.bee.games.utils.BaseGameActivity;
import com.apisense.bee.games.utils.GameHelper;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.Achievements;

import fr.inria.asl.utils.Log;

public class BeeGameManager implements GameManagerInterface {

    public static final String MISSIONS_LEADERBOARD_ID = "CgkIl-DToIgLEAIQBA";
    private static BeeGameManager instance;

    private BaseGameActivity currentActivity;
    private int achievementUnlockCount;
    private int achievementLockCount;

    private BeeGameManager() {
        this.currentActivity = null;
        this.achievementUnlockCount = 0;
        this.achievementLockCount = 0;
    }

    public static BeeGameManager getInstance() {
        if (instance == null) {
            instance = new BeeGameManager();
        }
        return instance;
    }

    @Override
    public void initialize(BaseGameActivity currentActivity) {
        this.currentActivity = currentActivity;

        GameHelper gh = new GameHelper(this.currentActivity, GameHelper.CLIENT_GAMES);
        gh.setup(this.currentActivity);
        gh.enableDebugLog(true);
        gh.setConnectOnStart(true);
        this.currentActivity.setGameHelper(gh);

        Log.getInstance().i("BeeGameManager : Loading player data ... : " + this.loadGameData());
    }


    public boolean loadGameData() {
        if (this.currentActivity != null) {
            // load achievements
            Games.Achievements.load(this.currentActivity.getApiClient(), true).setResultCallback(new ResultCallback<Achievements.LoadAchievementsResult>() {
                @Override
                public void onResult(Achievements.LoadAchievementsResult loadAchievementsResult) {
                    for (Achievement achievement : loadAchievementsResult.getAchievements()) {

                        if (achievement.getState() == Achievement.STATE_UNLOCKED) {
                            achievementUnlockCount++;
                        } else {
                            achievementLockCount++;
                        }

                        Log.getInstance().i("BeeGameManager : Achievement=" + achievement.getName() + "&status=" + achievement.getState());
                    }

                    Log.getInstance().i("BeeGameManager : Handle method onResult for loadGameData");

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

        if (gameAchievement.isIncremental()) {
            Games.Achievements.increment(this.currentActivity.getApiClient(), gameAchievement.getId(), gameAchievement.getIncrementPart());
        }
        Games.Achievements.unlock(this.currentActivity.getApiClient(), gameAchievement.getId());
        Log.getInstance().i("BeeGameManager : GPG Push Achievement : " + gameAchievement);
    }

    @Override
    public Intent getAchievements() {
        return Games.Achievements.getAchievementsIntent(this.currentActivity.getApiClient());
    }

    @Override
    public void pushScore(String leardboardId, int score) {
        if (!isConnected())
            return;

        Games.Leaderboards.submitScore(this.currentActivity.getApiClient(), leardboardId, score);
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
        return this.achievementUnlockCount;
    }

    public int getAchievementLockCount() {
        return this.achievementLockCount;
    }

}
