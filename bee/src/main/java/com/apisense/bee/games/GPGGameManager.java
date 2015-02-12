package com.apisense.bee.games;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.apisense.bee.games.action.GameAchievement;
import com.apisense.bee.games.utils.BaseGameUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import fr.inria.asl.utils.Log;

public class GPGGameManager implements GameManagerInterface {

    public static final int RC_SIGN_IN = 9001;
    // Request code to use when launching the resolution activity
    public static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    public static final String DIALOG_ERROR = "dialog_error";

    public static final String MISSIONS_LEADERBOARD_ID = "CgkIl-DToIgLEAIQBA";

    private static GPGGameManager instance;

    private Activity currentContext;
    private GoogleApiClient mGoogleApiClient;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked;

    private GPGGameManager() {
        this.mSignInClicked = false;
    }

    public static GPGGameManager getInstance() {
        if (instance == null) {
            instance = new GPGGameManager();
        }
        return instance;
    }

    @Override
    public boolean initialize(Activity context) {
        // Bind the app currentContext
        this.currentContext = context;

        if (this.mGoogleApiClient == null) {
            // Create the Google Api Client with access to the Play Game services
            mGoogleApiClient = new GoogleApiClient.Builder(this.currentContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Games.API).addScope(Games.SCOPE_GAMES).addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();


            return true;
        }
        return true;
    }

    @Override
    public void pushAchievement(GameAchievement gameAchievement) {
        if (!isConnected())
            return;

        if (gameAchievement.isIncremental()) {
            Games.Achievements.increment(mGoogleApiClient, gameAchievement.getId(), gameAchievement.getIncrementPart());
        }
        Games.Achievements.unlock(mGoogleApiClient, gameAchievement.getId());
        Log.getInstance().i("GPG Push Achievement : " + gameAchievement);
    }

    @Override
    public Intent getAchievements() {
        return Games.Achievements.getAchievementsIntent(mGoogleApiClient);
    }

    @Override
    public void pushScore(String leardboardId, int score) {
        if (!isConnected())
            return;

        Games.Leaderboards.submitScore(mGoogleApiClient, leardboardId, score);
    }

    @Override
    public Intent getLeaderboard(String leaderboardId) {
        return Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                leaderboardId);
    }

    @Override
    public boolean isConnected() {
        return this.mGoogleApiClient.isConnected();

    }

    @Override
    public boolean signin() {
        if (this.isConnected()) return false;
        this.mSignInClicked = true;
        return this.connect();
    }

    @Override
    public boolean signout() {
        if (!this.isConnected()) return false;
        // sign out.
        this.mSignInClicked = false;
        Games.signOut(mGoogleApiClient);

        return this.disconnect();
    }

    @Override
    public boolean connect() {
        if (this.currentContext == null) return false;
        mGoogleApiClient.connect();

        return true;
    }

    @Override
    public boolean disconnect() {
        if (this.currentContext == null) return false;

        mGoogleApiClient.disconnect();
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("Bee GPG", "GPG Connected");

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i("Bee GPG", "GPG Connection Suspended");


        mGoogleApiClient.connect(); // Retry
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // Already resolving
            return;
        }

        // If the sign in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            Log.i("Bee GPG", "GPG resolving error");


            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(currentContext,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, "error")) {
                Log.i("Bee GPG", "GPG resolving error failed");

                mResolvingConnectionFailure = false;
            }
        }
    }

    public boolean isResolvingError() {
        return this.mResolvingConnectionFailure;
    }

    public void setResolvingStatus(boolean resolving) {
        this.mResolvingConnectionFailure = resolving;

    }

    public boolean isConnecting() {
        return this.mGoogleApiClient.isConnecting();

    }

    private void showErrorDialog(int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);

        FragmentManager fragmentManager = ((FragmentActivity) this.currentContext).getSupportFragmentManager();
        dialogFragment.show(fragmentManager, "errordialog");
    }

    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {

            //((SlideshowActivity)getActivity()).onDialogDismissed();
        }
    }

}
