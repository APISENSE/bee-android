package com.apisense.bee.backend.user;

import android.content.Context;
import com.apisense.android.api.APS;
import com.apisense.core.api.Callback;
import com.apisense.core.api.Log;


/**
* Represents an asynchronous Sign out task used to de-authenticate the user.
*
*/
public class SignOutTask {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private Callback<Void> listener;

    public SignOutTask(Context context, Callback<Void> listener) {
        this.context = context;
        this.listener = listener;
    }

    public void execute() {
        try {
            APS.disconnect(context);

            listener.onCall(null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e);
            listener.onError(e);
        }
    }
}
