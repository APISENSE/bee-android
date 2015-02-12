package com.apisense.bee.games.action;

/**
 * Created by Warnant on 12-02-15.
 */
public class ShareAceGameAchievement extends GameAchievement {

    private static final String SHARE_ACE_GPG_ID_KEY = "CgkIl-DToIgLEAIQAw";

    public ShareAceGameAchievement() {
        super(SHARE_ACE_GPG_ID_KEY, false);
        this.name = this.getClass().getName();
    }

    @Override
    public boolean perform() {
        return true;
    }
}
