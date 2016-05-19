package com.apisense.bee.games;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.apisense.bee.R;
import com.apisense.bee.games.utils.BaseGameActivity;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.Achievements;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to encapsulate the default Play Games activity.
 * Also handle generic data retrieval about player and game statistics.
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public abstract class BeeGameActivity extends BaseGameActivity {
    private static final String TAG = "BeeGameActivity";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        // Disable auto-login
        getGameHelper().setMaxAutoSignInAttempts(0);
    }

    protected interface Pending<T> {
        /**
         * Defines the reaction of the UI when the data are found.
         */
        void onFetched(T object);
    }

    @Override
    public void onSignInSucceeded() {
        new SimpleGameAchievement(getString(R.string.achievement_new_bee)).unlock(this);
    }

    @Override
    public void onSignInFailed() {
        // Nothing
    }

    public void refreshPlayGamesData(Pending<BeePlayer> onPlayerFetched) {
        if (isSignedIn()) {
            new RetrievePlayerData(onPlayerFetched).execute();
        } else {
            Log.w(TAG, "User not connected to GPG yet, skip data refresh");
        }
    }

    public void refreshAchievements(Pending<List<Achievement>> onAchievementsFetched) {
        if (isSignedIn()) {
            new RetrieveAchievements(onAchievementsFetched).execute();
        } else {
            Log.w(TAG, "User not connected to GPG yet, skip achievements refresh");
        }
    }

    /**
     * This method returns the count of finished achievements on the game
     */
    protected static int countUnlocked(List<Achievement> currentAchievements) {
        int count = 0;
        for (Achievement achievement : currentAchievements) {
            if (achievement.getState() == Achievement.STATE_UNLOCKED) {
                count++;
            }
        }
        return count;
    }

    private class RetrievePlayerData extends AsyncTask<Void, Void, BeePlayer> {
        private Pending<BeePlayer> onPlayerFetched;

        public RetrievePlayerData(Pending<BeePlayer> onPlayerFetched) {

            this.onPlayerFetched = onPlayerFetched;
        }

        @Override
        protected BeePlayer doInBackground(Void... params) {
            // Retrieve data about player
            Player player = Games.Players.getCurrentPlayer(getApiClient());

            String username = player.getDisplayName();
            Uri userImage = player.getIconImageUri();

            return new BeePlayer(username, userImage);
        }

        @Override
        protected void onPostExecute(BeePlayer player) {
            if (onPlayerFetched != null) {
                onPlayerFetched.onFetched(player);
            }
        }
    }

    private class RetrieveAchievements extends AsyncTask<Void, Void, Void> {
        private Pending<List<Achievement>> onAchievementsFetched;

        public RetrieveAchievements(Pending<List<Achievement>> onAchievementsFetched) {
            this.onAchievementsFetched = onAchievementsFetched;
        }

        @Override
        protected Void doInBackground(Void... params) {

            PendingResult<Achievements.LoadAchievementsResult> loadAchievementsResult = Games.Achievements.load(getApiClient(), false); // true will disable cache usage.
            loadAchievementsResult.setResultCallback(new ResultCallback<Achievements.LoadAchievementsResult>() {
                @Override
                public void onResult(@NonNull Achievements.LoadAchievementsResult loadAchievementsResult) {
                    final List<Achievement> currentAchievements = new ArrayList<>();

                    DataBuffer<Achievement> achievementBuffer = loadAchievementsResult.getAchievements();
                    for (Achievement achievement : achievementBuffer) {
                        Log.v(TAG, "Achievement=" + achievement.getName() + "&status=" + achievement.getState());
                        currentAchievements.add(achievement);
                    }

                    if (onAchievementsFetched != null) {
                        onAchievementsFetched.onFetched(currentAchievements);
                    }

                    // Close buffers, achievement no more accessible
                    achievementBuffer.release();
                    loadAchievementsResult.release();
                }
            });
            return null;
        }
    }

}
