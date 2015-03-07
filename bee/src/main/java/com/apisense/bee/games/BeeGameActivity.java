package com.apisense.bee.games;

import android.os.Bundle;

import com.apisense.bee.games.utils.BaseGameActivity;

/**
 * Created by Warnant on 19-02-15.
 */
public class BeeGameActivity extends BaseGameActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        BeeGameManager.getInstance().initialize(this);
        getGameHelper().setMaxAutoSignInAttempts(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BeeGameManager.getInstance().refreshPlayerData();
    }

    @Override
    public void onSignInFailed() {
        //TODO
    }

    @Override
    public void onSignInSucceeded() {
        //TODO
    }
}
