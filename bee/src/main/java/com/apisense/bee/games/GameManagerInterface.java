package com.apisense.bee.games;


import android.content.Intent;

import com.apisense.bee.games.action.GameAchievement;
import com.apisense.bee.games.utils.BaseGameActivity;

/**
 * This interface describes the Game Manager interface used to handle all game actions in the app
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public interface GameManagerInterface {

    /**
     * This method is used to initialize the game manager context with the current activity
     *
     * @param baseGameActivity BeeGameActivity the activity context
     */
    public void initialize(BaseGameActivity baseGameActivity);

    /**
     * This method is used to refresh the current player data from the Play Games in the application
     *
     * @return boolean true if the data have been updated, false otherwise
     */
    public boolean refreshPlayerData();

    /**
     * This method returns the current state of the game manager connection with the Play Games
     *
     * @return boolean true if the manager is connected to the Play Games, false otherwise
     */
    public boolean isConnected();

    /**
     * This method is used to push a new achievement state in the remote Play Games platform
     *
     * @param achievement GameAchievement the achievement to push
     */
    public void pushAchievement(GameAchievement achievement);

    /**
     * This method returns the game achievement object associated to the achievement id
     *
     * @param achievementId String the requested achievement id
     * @return GameAchievement the achievement found
     */
    public GameAchievement getAchievement(String achievementId);

    /**
     * This method returns the achievement intent used to see all achievements on the Play Games
     *
     * @return Intent the achievement intent
     */
    public Intent getAchievementList();

    /**
     * This method is used to push remotely a score update in a specific leaderboard on the Play Games
     *
     * @param leardboardId String the requested leaderboard ID
     * @param score        int the new score count
     */
    public void pushScore(String leardboardId, int score);

    /**
     * This method returns the leaderboard intent used to see a specific leaderboard on the Play Games
     *
     * @param leaderboardId String the requested leaderboard ID
     * @return Intent the leaderboard intent
     */
    public Intent getLeaderboard(String leaderboardId);
}

