package com.apisense.bee.callbacks;

import android.content.Context;
import android.content.Intent;

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

    private Context context;

    public BeeAPSCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onError(Exception e) {
        // Retrofit encapsulate the APISENSE Exception.
        if (e.getCause() instanceof UserNotConnectedException) {
            Intent loginIntent = new Intent(context, SignInActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loginIntent.putExtra(SignInActivity.ON_THE_FLY, true);
            context.startActivity(loginIntent);
            return;
        }
    }
}
