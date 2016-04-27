package com.apisense.bee.callbacks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.bee.ui.activity.SignInActivity;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.exception.UserNotConnectedException;

/**
 * Common implementation of the APSCallback
 * handling generic errors.
 * At the moment, this callback will:
 * - ask the user to log back in if the session is invalid when contacting server.
 *
 * @param <T> Type of the returned object.
 */
public abstract class BeeAPSCallback<T> implements APSCallback<T> {
    protected Activity activity;

    public BeeAPSCallback(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onError(Exception e) {
        // Retrofit encapsulate the APISENSE Exception.
        if (e.getCause() instanceof UserNotConnectedException) {
            Intent loginIntent = new Intent(activity, SignInActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loginIntent.putExtra(SignInActivity.ON_THE_FLY, true);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(activity, R.string.error_invalid_session, Toast.LENGTH_LONG).show();
            activity.startActivity(loginIntent);
            activity.finish();
        }
    }
}
