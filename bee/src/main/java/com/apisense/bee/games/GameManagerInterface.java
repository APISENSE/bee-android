package com.apisense.bee.games;


import android.content.Intent;

import com.apisense.bee.games.action.GameAchievement;
import com.apisense.bee.games.utils.BaseGameActivity;

public interface GameManagerInterface {

    public void initialize(BaseGameActivity baseGameActivity);

    public boolean refreshPlayerData();

    public boolean isLoad();

    public void pushAchievement(GameAchievement achievement);

    public GameAchievement getAchievement(String achievementId);

    public Intent getAchievementList();

    public void pushScore(String leardboardId, int score);

    public Intent getLeaderboard(String leaderboardId);
}

