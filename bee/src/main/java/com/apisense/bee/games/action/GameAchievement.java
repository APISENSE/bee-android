package com.apisense.bee.games.action;

import com.google.android.gms.games.achievement.Achievement;

/**
 * Created by Warnant on 12-02-15.
 */
public class GameAchievement implements GameAction {

    protected Achievement gpgAchievement;
    protected String leadboard;

    public GameAchievement(Achievement achievement) {
        this.gpgAchievement = achievement;
        this.leadboard = null;
    }

    public String getLeadboard() {
        return this.leadboard;
    }

    public void setLeadboard(String leadboard) {
        this.leadboard = leadboard;
    }

    protected Achievement getGpgAchievement() {
        return this.gpgAchievement;
    }

    public String getId() {
        return this.gpgAchievement.getAchievementId();
    }

    public String getName() {
        return this.gpgAchievement.getName();
    }

    public boolean isIncremental() {
        return this.gpgAchievement.getType() == Achievement.TYPE_INCREMENTAL;
    }

    public boolean isFinished() {
        return this.gpgAchievement.getState() == Achievement.STATE_UNLOCKED;
    }

    public int getCurrentSteps() {
        if (!this.isIncremental()) {
            return 0;
        }
        return this.gpgAchievement.getCurrentSteps();
    }

    public int getTotalSteps() {
        if (!this.isIncremental()) {
            return 0;
        }
        return this.gpgAchievement.getTotalSteps();
    }

    @Override
    public String toString() {
        return "id=" + this.gpgAchievement.getAchievementId() +
                ", name=" + this.gpgAchievement.getName() +
                ", isIncremental=" + this.isIncremental();
    }

    @Override
    public int getScore() {
        return -1;
    }
}
