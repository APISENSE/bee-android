package com.apisense.bee.games.action;

import com.apisense.bee.games.BeeGameManager;

/**
 * Created by Warnant on 12-02-15.
 */
public class ShareAceAchievement extends GameAchievement {

    private static final String SHARE_ACE_GPG_ID_KEY = "CgkIl-DToIgLEAIQAw";

    public ShareAceAchievement() {
        super(BeeGameManager.getInstance().getAchievement(SHARE_ACE_GPG_ID_KEY).getGpgAchievement());

    }

    @Override
    public int getScore() {
        return 1;
    }
}
