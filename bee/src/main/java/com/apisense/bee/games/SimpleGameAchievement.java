package com.apisense.bee.games;

import com.apisense.bee.games.BeeGameActivity;
import com.google.android.gms.games.Games;

public class SimpleGameAchievement {
    private String achievementID;

    public SimpleGameAchievement(String achievementID) {
        this.achievementID = achievementID;
    }

    public void unlock(BeeGameActivity fromActivity) {
        Games.Achievements.unlock(fromActivity.getApiClient(), achievementID);
        BeeGameManager.getInstance().refreshPlayerData();
    }
}
