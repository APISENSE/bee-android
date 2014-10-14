package com.apisense.bee.backend.user;

import android.os.AsyncTask;
import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;

public class RegisterTask extends AsyncTaskWithCallback {
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTasksCallbacks listener;

    public RegisterTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected Integer doInBackground(String... params) {
        // params[0] == pseudo
        // params[1] == password
        // params[2] == URL hive (optionnal)
        String pseudo = "";
        String password = "";
        String apisenseUrl = (params.length >= 3) ? params[2] : BeeApplication.BEE_DEFAULT_URL;

        if (params[0].isEmpty() || params[1].isEmpty()) {
            Log.e(TAG, "Login or password is empty");
            this.details = "EMPTY_FIELD -- To XMLify";
            return BeeApplication.ASYNC_ERROR;
        } else {
            pseudo = params[0];
            password = params[1];
        }

        try {
            APISENSE.apisServerService().createAccount(apisenseUrl, "", pseudo, password, "");
            APISENSE.apisServerService().connect(pseudo, password);
        } catch (Exception e) {
            e.printStackTrace();
            this.details = e.getMessage();
        }

        if (!APISENSE.apisServerService().isConnected())
            return BeeApplication.ASYNC_ERROR;
        else
            return BeeApplication.ASYNC_SUCCESS;
    }
}