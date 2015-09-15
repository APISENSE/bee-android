package com.apisense.bee.games;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.games.Games;

public class IncrementalGameAchievement {
    private static final String TAG = "IncrementalAchievement";
    private final String achievementID;

    public IncrementalGameAchievement(String achievementID) {
        this.achievementID = achievementID;
    }

    public void increment(BeeGameActivity fromActivity) {
        if (fromActivity.getGameHelper().isSignedIn()) {
            Log.d(TAG, "Increasing achievement value: " + achievementID);
            Games.Achievements.increment(fromActivity.getApiClient(), achievementID, 1);
        }
    }

    public void increment(Fragment fromFragment){
        Activity hostActivity = fromFragment.getActivity();
        if (hostActivity instanceof BeeGameActivity) {
            increment((BeeGameActivity) hostActivity);
        }
    }
}
