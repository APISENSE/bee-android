package com.apisense.bee.backend.user;

import android.os.AsyncTask;
import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;

/**
 * Represents an asynchronous registration task used to create a new user.
 */

public class RegisterTask extends AsyncTaskWithCallback<String, Void, String> {
    private final String TAG = this.getClass().getSimpleName();

    public RegisterTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... params) {
        // params[0] == pseudo
        // params[1] == password
        // params[2] == URL hive (optionnal)
        String pseudo = "";
        String password = "";
        String apisenseUrl = (params.length >= 3) ? params[2] : BeeApplication.BEE_DEFAULT_URL;
        String details = "";

        if (params[0].isEmpty() || params[1].isEmpty()) {
            Log.e(TAG, "Login or password is empty");
            this.errcode = BeeApplication.ASYNC_ERROR;
            details = "EMPTY_FIELD -- To XMLify";
        } else {
            pseudo = params[0];
            password = params[1];
            try {
                APISENSE.apisServerService().createAccount(apisenseUrl, "", pseudo, password, "");
                APISENSE.apisServerService().connect(pseudo, password);
            } catch (Exception e) {
                e.printStackTrace();
                details = e.getMessage();
            }

            if (!APISENSE.apisServerService().isConnected()) {
                this.errcode = BeeApplication.ASYNC_ERROR;
            } else {
                this.errcode = BeeApplication.ASYNC_SUCCESS;
            }
        }
        return details;
    }

}