package com.apisense.bee.games;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class GPGGameManager implements GameManagerInterface {

    public static final int RC_SIGN_IN = 9001;
    // Request code to use when launching the resolution activity
    public static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    public static final String DIALOG_ERROR = "dialog_error";

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
        if (this.currentContext != null) return false;

        // Bind the app currentContext
        this.currentContext = context;

        // Create the Google Api Client with access to the Play Game services
        mGoogleApiClient = new GoogleApiClient.Builder(this.currentContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        return true;
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

    public boolean isConnected() {
        return this.mGoogleApiClient.isConnected();

    }

    public boolean signin() {
        this.mSignInClicked = true;
        return this.connect();
    }

    public boolean signout() {
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
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingConnectionFailure = true;
                connectionResult.startResolutionForResult(currentContext, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingConnectionFailure = true;
        }

        Log.i("Bee GPG", "GPG Connection Failed. ErrorCode=" + connectionResult.getErrorCode());

        // Put code here to display the sign-in button
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
