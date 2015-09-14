package com.apisense.bee.games;


import android.content.Intent;

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
    void initialize(BaseGameActivity baseGameActivity);

    /**
     * This method is used to refresh the current player data from the Play Games in the application
     *
     * @return boolean true if the data have been updated, false otherwise
     */
    boolean refreshPlayerData();

    /**
     * This method returns the current state of the game manager connection with the Play Games
     *
     * @return boolean true if the manager is connected to the Play Games, false otherwise
     */
    boolean alreadySignedIn();


    /**
     * This method returns the achievement intent used to see all achievements on the Play Games
     *
     * @return Intent the achievement intent
     */
    Intent getAchievementListIntent();
}

