package com.apisense.bee.games;


import android.app.Activity;
import android.content.Intent;

import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.common.api.GoogleApiClient;

public interface GameManagerInterface extends GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public boolean initialize(Activity context);
    public boolean connect();
    public boolean disconnect();

    public boolean signin();

    public boolean signout();

    public boolean isConnected();

    public void pushAchievement(GameAchievement achievement);

    public Intent getAchievements();

    public void pushScore(String leardboardId, int score);

    public Intent getLeaderboard(String leaderboardId);
}

