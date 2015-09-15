package com.apisense.bee.games;

import com.google.android.gms.games.Games;

public class IncrementalGameAchievement {
    private final String achievementID;

    public IncrementalGameAchievement(String achievementID) {
        this.achievementID = achievementID;
    }

    public void increment(BeeGameActivity fromActivity) {
        Games.Achievements.increment(fromActivity.getApiClient(), achievementID, 1);
    }
}
