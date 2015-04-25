package com.apisense.bee.games.action;

/**
 * This interface describes the base methods for a game action in the application
 * For instance, this action could be a game achievement in the game
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public interface GameAction {

    /**
     * This method returns the ID of the game action
     *
     * @return String the action ID
     */
    public String getId();

    /**
     * This method returns the name of the game action
     *
     * @return String the name of the action
     */
    public String getName();

    /**
     * This method returns the score of the action reward when the action is completed
     *
     * @return int the score
     */
    public int getScore();

}
