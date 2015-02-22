package com.apisense.bee.games.action;

import com.apisense.bee.games.BeeGameManager;
import com.google.android.gms.games.achievement.Achievement;

/**
 * Created by Warnant on 12-02-15.
 */
public abstract class GameAchievement implements GameAction {

    public static final String SHARE_ACE_KEY = "CgkIl-DToIgLEAIQAw";
    public static final String GOOGLE_SIGN_IN_KEY = "CgkIl-DToIgLEAIQAQ";
    public static final String FACEBOOK_SIGN_IN_KEY = "CgkIl-DToIgLEAIQBg";
    public static final String FIRST_MISSION_KEY = "CgkIl-DToIgLEAIQAg";
    public static final String CROWD_SENSING_ACE_KEY = "CgkIl-DToIgLEAIQBQ";
    public static final String CROWD_SENSING_PARTNER_KEY = "CgkIl-DToIgLEAIQBw";
    public static final String CROWD_SENSING_SPECIALIST_KEY = "CgkIl-DToIgLEAIQCA";


    protected Achievement gpgAchievement;
    protected String leadboard;


    public GameAchievement(Achievement achievement) {
        this.gpgAchievement = achievement;
        this.leadboard = null;
        this.load();
    }

    public boolean load() {
        setLeadboard(BeeGameManager.MISSIONS_LEADERBOARD_ID);
        return true;
    }

    public long getPoints() {
        return this.gpgAchievement.getXpValue() / 100;
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


    public abstract boolean process();
}
