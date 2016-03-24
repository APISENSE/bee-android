package com.apisense.bee.games;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.apisense.bee.R;
import com.apisense.bee.games.utils.BaseGameActivity;
import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.Achievements;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to encapsulate the default Play Games activity.
 * Also handle generic data retrieval about player and game statistics.
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public abstract class BeeGameActivity extends BaseGameActivity {
    private static final String TAG = "BeeGameActivity";

    private static String username;
    private static Drawable userImage;
    protected static int unlockedCount = 0;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        // Disable auto-login
        getGameHelper().setMaxAutoSignInAttempts(0);
    }

    /**
     * Defines the reaction of the UI when the GPG data are found.
     */
    protected void onPlayGamesDataRecovered() {
        // To override if needed
    }

    @Override
    public void onSignInSucceeded() {
        new SimpleGameAchievement(getString(R.string.achievement_new_bee)).unlock(this);
        if (firstConnection()) {
            refreshPlayGamesData();
        }
    }

    private boolean firstConnection() {
        return username == null && userImage == null;
    }

    @Override
    public void onSignInFailed() {
        // Nothing
    }

    public void refreshPlayGamesData() {
        new RetrievePlayGamesData().execute();
    }

    protected String getUserDisplayName() {
        return username != null ? username : getResources().getString(R.string.anonymous_user);
    }

    protected Drawable getUserImage() {
        return userImage != null ? userImage : getResources().getDrawable(R.drawable.ic_launcher_bee);
    }

    private class RetrievePlayGamesData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Retrieve data about player
            Player player = Games.Players.getCurrentPlayer(getApiClient());
            username = player.getDisplayName();

            if (userImage == null) { // Avoid retrieval of image on each refresh of the activity
                userImage = retrieveDrawableFromLocation(player.getIconImageUrl());
            }

            // Retrieve data about achievements
            refreshAchievements();
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            onPlayGamesDataRecovered();
        }

        private Drawable retrieveDrawableFromLocation(String location) {
            try {
                Log.d(TAG, "Retrieving user image from location: " + location);
                InputStream is = (InputStream) new URL(location).getContent();
                return Drawable.createFromStream(is, null);
            } catch (Exception e) {
                Log.w(TAG, "Unable to retrieve user icon: " + e.getMessage());
                return null;
            }
        }

        private void refreshAchievements() {
            final List<Achievement> currentAchievements = new ArrayList<>();
            Achievements.LoadAchievementsResult loadAchievementsResult = Games.Achievements.load(getApiClient(), false).await(10, TimeUnit.SECONDS); // true will disable cache usage.
            DataBuffer<Achievement> achievementBuffer = loadAchievementsResult.getAchievements();

            for (Achievement achievement : achievementBuffer) {
                Log.d(TAG, "Achievement=" + achievement.getName() + "&status=" + achievement.getState());
                currentAchievements.add(achievement);
            }

            // Update data about achievements
            updateUnlockedAchievementsCount(currentAchievements);

            // Close buffers, achievement no more accessible
            achievementBuffer.close();
            loadAchievementsResult.release();
        }

        /**
         * This method returns the count of finished achievements on the game
         *
         * @return int the current count of finished achievements
         */
        private void updateUnlockedAchievementsCount(List<Achievement> currentAchievements) {
            int count = 0;
            for (Achievement achievement : currentAchievements) {
                if (achievement.getState() == Achievement.STATE_UNLOCKED) {
                    count++;
                }
            }
            unlockedCount = count;
        }
    }
}
