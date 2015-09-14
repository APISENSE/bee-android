package com.apisense.bee.games;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.apisense.bee.R;
import com.apisense.bee.games.utils.BaseGameActivity;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * This class is used to encapsulate the default Play Games activity.
 * The class initializes the BeeGameManager
 * and the Google Play Games helper provided by the Google Team.
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public abstract class BeeGameActivity extends BaseGameActivity {
    private static final String TAG = "BeeGameActivity";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        BeeGameManager gameManager = BeeGameManager.getInstance();
        gameManager.initialize(this);
        if (gameManager.alreadySignedIn()) {
            gameManager.connectPlayer();
        }
        getGameHelper().setMaxAutoSignInAttempts(0);
    }

    @Override
    public void onSignInFailed() {
        //TODO
    }

    @Override
    public void onSignInSucceeded() {
        new SimpleGameAchievement(getString(R.string.achievement_new_bee)).unlock(this);

        // Get the person data
        Plus.PeopleApi.loadConnected(BeeGameManager.getInstance().getGoogleApiClient())
                .setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                    @Override
                    public void onResult(People.LoadPeopleResult loadPeopleResult) {
                        Log.v(TAG, "Got user result:" + loadPeopleResult);
                        if (loadPeopleResult.getPersonBuffer().getCount() == 0) {
                            return;
                        }

                        Person currentPlayer = loadPeopleResult.getPersonBuffer().get(0);
                        Log.d(TAG, "Got actual user:" + currentPlayer.toString());
                        // Set username name after sign in
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("username", currentPlayer.getDisplayName());
                        editor.apply();
                    }
                });


        BeeGameManager.getInstance().refreshPlayerData();
    }
}
