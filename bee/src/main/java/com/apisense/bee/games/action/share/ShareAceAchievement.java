package com.apisense.bee.games.action.share;

import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

/**
 * This class represents the methods of a specialized achievement of app sharing
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public class ShareAceAchievement extends GameAchievement {

    /**
     * Constructor
     *
     * @param achievement Achievement the official achievement object
     */
    public ShareAceAchievement(Achievement achievement) {
        super(achievement);
    }

    /**
     * @see com.apisense.bee.games.action.GameAchievement
     */
    @Override
    public boolean process() {
        return true;
    }

    /**
     * @see com.apisense.bee.games.action.GameAchievement
     */
    @Override
    public int getScore() {
        return 1;
    }

    /**
     * @see com.apisense.bee.games.action.GameAchievement
     */
    @Override
    public long getPoints() {
        return 2 * super.getPoints();
    }
}

