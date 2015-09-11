package com.apisense.bee.games.action;

import com.apisense.bee.games.BeeGameManager;
import com.google.android.gms.games.achievement.Achievement;

/**
 * This abstract class represents the base methods of a game achievement in the application
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public abstract class GameAchievement implements GameAction {
    /**
     * The official achievement object from the Google Play Games library
     */
    protected Achievement gpgAchievement;

    /**
     * The associated leaderboard of the current achievement in the Play Games
     */
    protected String leadboard;

    /**
     * Constructor
     *
     * @param achievement Achievement the official Achievement object from the Play Games library
     */
    public GameAchievement(Achievement achievement) {
        this.gpgAchievement = achievement;
        this.leadboard = null;
        this.load();
    }

    /**
     * This method load the base achievement date such as the associated leaderboard
     *
     * @return true if the game data has been charged, false otherwise
     */
    public boolean load() {
        if (this.leadboard != null) {
            return false;
        }
        setLeadboard(BeeGameManager.MISSIONS_LEADERBOARD_ID);
        return true;
    }

    /**
     * This method returns the reward points (experience) when the the player has finished an achievement
     * By default, it this method is not override, the points are set to 5.
     *
     * @return int the points
     */
    public long getPoints() {
        if (this.gpgAchievement.getXpValue() != 0)
            return this.gpgAchievement.getXpValue();

        return 5;
    }

    /**
     * This method returns the current leaderboard associated to the achievement
     *
     * @return String the achievement ID
     */
    public String getLeadboard() {
        return this.leadboard;
    }

    /**
     * This method set the current leaderboard to the game achievement
     *
     * @param leadboard String the new leaderboard ID
     */
    public void setLeadboard(String leadboard) {
        this.leadboard = leadboard;
    }

    /**
     * This method returns the state of the current game achievement
     *
     * @return boolean true if the current achievement is finished, false otherwise
     */
    public boolean isFinished() {
        return this.gpgAchievement.getState() == Achievement.STATE_UNLOCKED;
    }

    /**
     * This method returns the current step of the achievement completition
     * This method only works if the achievement is an incremental achievement in the Play Games console.
     *
     * @return int the current step of the achievement
     */
    public int getCurrentSteps() {
        if (!this.isIncremental()) {
            return 0;
        }
        return this.gpgAchievement.getCurrentSteps();
    }

    /**
     * This method returns if the current achievement is incremental
     *
     * @return boolean true if the achievement is incremental, false otherwise
     */
    public boolean isIncremental() {
        return this.gpgAchievement.getType() == Achievement.TYPE_INCREMENTAL;
    }

    /**
     * This method tests if the achievement is completed and must contain all additional processing when an achievement is finished
     *
     * @return boolean true if the achievement has been completed, false otherwise
     */
    public abstract boolean process();

    @Override
    public String getId() {
        return this.gpgAchievement.getAchievementId();
    }

    @Override
    public String getName() {
        return this.gpgAchievement.getName();
    }

    @Override
    public String toString() {
        return "id=" + this.gpgAchievement.getAchievementId() +
                ", name=" + this.gpgAchievement.getName() +
                ", isIncremental=" + this.isIncremental() +
                ", points=" + this.getPoints();
    }

}
