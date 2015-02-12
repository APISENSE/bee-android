package com.apisense.bee.games.action;

/**
 * Created by Warnant on 12-02-15.
 */
public class SignInGameAchievement extends GameAchievement {

    private static final String SIGN_IN_GPG_ID_KEY = "CgkIl-DToIgLEAIQAQ";

    public SignInGameAchievement() {
        super(SIGN_IN_GPG_ID_KEY, false);
        this.name = this.getClass().getName();
    }

    @Override
    public boolean perform() {
        return true;
    }
}
