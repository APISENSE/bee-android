package com.apisense.bee.games;

import com.apisense.bee.games.utils.BaseGameActivity;

/**
 * Created by Warnant on 19-02-15.
 */
public class BeeGameActivity extends BaseGameActivity {


    @Override
    public void onSignInFailed() {
        //TODO
    }

    @Override
    public void onSignInSucceeded() {
        //TODO
    }

    @Override
    protected void onResume() {
        BeeGameManager.getInstance().refreshPlayerData();

        super.onResume();

    }
}
