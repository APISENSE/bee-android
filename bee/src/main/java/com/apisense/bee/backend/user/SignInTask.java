package com.apisense.bee.backend.user;

import android.os.AsyncTask;
import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */

public class SignInTask extends AsyncTaskWithCallback {
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTasksCallbacks listener;

    public SignInTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected Integer doInBackground(String... params) {
        // params[0] == login
        // params[1] == password
        // params[2] == URL hive (optionnal)
        if (params[0].isEmpty() || params[1].isEmpty()) {
            Log.e(this.TAG, "login or password is empty");
            this.details = "EMPTY_FIELD -- To XMLify";
            return BeeApplication.ASYNC_ERROR;
        }

        try {
            if (!params[2].isEmpty())
                APISENSE.apisServerService().setCentralHost(params[2]);
            else
                APISENSE.apisServerService().setCentralHost(BeeApplication.BEE_DEFAULT_URL);
            APISENSE.apisServerService().connect(params[0], params[1]);
        } catch (Exception e) {
            e.printStackTrace();
            this.details = e.getMessage();
        }

        if (!APISENSE.apisServerService().isConnected())
            return BeeApplication.ASYNC_ERROR;
        else {
            try {
                APISENSE.apisServerService().updateUserAccount();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return BeeApplication.ASYNC_SUCCESS;
        }

    }
}
