package com.apisense.bee.games.action;

import com.apisense.bee.games.BeeGameManager;

/**
 * Created by Warnant on 12-02-15.
 */
public class SignInAchievement extends GameAchievement {

    private static final String SIGN_IN_GPG_ID_KEY = "CgkIl-DToIgLEAIQAQ";

    public SignInAchievement() {
        super(BeeGameManager.getInstance().getAchievement(SIGN_IN_GPG_ID_KEY).getGpgAchievement());

    }

    @Override
    public int getScore() {
        return 1;
    }
}
