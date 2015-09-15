package com.apisense.bee.games;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.games.Games;

public class SimpleGameAchievement {
    private static final String TAG = "SimpleAchievement";
    private String achievementID;

    public SimpleGameAchievement(String achievementID) {
        this.achievementID = achievementID;
    }

    public void unlock(BeeGameActivity fromActivity) {
        if (fromActivity.getGameHelper().isSignedIn()) {
            Log.d(TAG, "Unlocking achievement: " + achievementID);
            Games.Achievements.unlock(fromActivity.getApiClient(), achievementID);
        }
    }

    public void unlock(Fragment fromFragment){
        Activity hostActivity = fromFragment.getActivity();
        if (hostActivity instanceof BeeGameActivity) {
            unlock((BeeGameActivity) hostActivity);
        }
    }
}
