package com.apisense.bee.games;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.apisense.bee.games.event.OnGameDataLoadedEvent;
import com.apisense.bee.games.event.SignInEvent;
import com.apisense.bee.games.utils.BaseGameActivity;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by Warnant on 19-02-15.
 */
public class BeeGameActivity extends BaseGameActivity implements OnGameDataLoadedListener {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        BeeGameManager.getInstance().addOnGameDataLoadedListener(this);
        BeeGameManager.getInstance().initialize(this);
        getGameHelper().setMaxAutoSignInAttempts(0);
    }

    @Override
    public void onSignInFailed() {
        //TODO
    }

    @Override
    public void onSignInSucceeded() {
        BeeGameManager.getInstance().fireGameEventPerformed(new SignInEvent(this));

        // Get the person data
        Plus.PeopleApi.loadConnected(BeeGameManager.getInstance().getGoogleApiClient()).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(People.LoadPeopleResult loadPeopleResult) {

                if (loadPeopleResult.getPersonBuffer().getCount() == 0) {
                    return;
                }

                Person currentPlayer = loadPeopleResult.getPersonBuffer().get(0);

                // Set username name after sign in
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", currentPlayer.getDisplayName());
                editor.apply();
            }
        });


        BeeGameManager.getInstance().refreshPlayerData();
    }

    @Override
    public void onRefresh(OnGameDataLoadedEvent event) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        BeeGameManager.getInstance().refreshPlayerData();
    }
}
