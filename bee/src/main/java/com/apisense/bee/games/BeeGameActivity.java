package com.apisense.bee.games;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.apisense.bee.R;
import com.apisense.bee.games.utils.BaseGameActivity;
import com.apisense.bee.games.utils.GameHelper;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to encapsulate the default Play Games activity.
 * The class initializes the BeeGameManager
 * and the Google Play Games helper provided by the Google Team.
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public abstract class BeeGameActivity extends BaseGameActivity {
    private static final String TAG = "BeeGameActivity";
    private Map<String, Achievement> currentAchievements = new HashMap<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
    }

    @Override
    public void onSignInSucceeded() {
        new SimpleGameAchievement(getString(R.string.achievement_new_bee)).unlock(this);

        refreshAchievements();
        String username = Games.getCurrentAccountName(getApiClient());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.apply();
    }

    @Override
    public void onSignInFailed() {
        //TODO
    }

    /**
     * This method returns the count of finished achievements on the game
     *
     * @return int the current count of finished achievements
     */
    protected int getUnlockedAchievementsCount() {
        int count = 0;
        for (Achievement achievement : currentAchievements.values()) {
            if (achievement.getState() == Achievement.STATE_UNLOCKED) {
                count++;
            }
        }
        return count;
    }

    protected void refreshAchievements() {
        if (isSignedIn()) {
            // load achievements
            Games.Achievements.load(getApiClient(), false) // true will disable cache usage.
                    .setResultCallback(new ResultCallback<Achievements.LoadAchievementsResult>() {
                        @Override
                        public void onResult(Achievements.LoadAchievementsResult loadAchievementsResult) {
                            Log.i(TAG, "Handle method onResult for refreshAchievements");
                            DataBuffer<Achievement> achievementBuffer = loadAchievementsResult.getAchievements();
                            for (Achievement achievement : achievementBuffer) {
                                Log.i(TAG, "Achievement=" + achievement.getName() + "&status=" + achievement.getState());
                                currentAchievements.put(achievement.getAchievementId(), achievement);
                            }
                            achievementBuffer.close();
                            loadAchievementsResult.release();
                        }
                    });
        }
    }
}
