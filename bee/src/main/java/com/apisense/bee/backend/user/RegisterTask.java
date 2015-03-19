package com.apisense.bee.backend.user;

import android.util.Log;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;

import fr.inria.apislog.APISLog;
import fr.inria.bsense.service.BSenseServerService;
import fr.inria.bsense.service.BeeSenseServiceManager;

/**
 * Represents an asynchronous registration task used to create a new user.
 */
public class RegisterTask extends AsyncTaskWithCallback<String, Void, String> {
    protected final BSenseServerService servService;
    private final String TAG = this.getClass().getSimpleName();

    public RegisterTask(BeeSenseServiceManager apiServices, AsyncTasksCallbacks listener) {
        super(listener);
        servService = apiServices.getBSenseServerService();
    }

    @Override
    protected String doInBackground(String... params) {
        // params[0] == pseudo
        // params[1] == password
        // params[2] == URL hive (optionnal)
        String details = "";

        if (params.length < 2) {
            Log.e(TAG, "Not enough parameters");
            this.errcode = BeeApplication.ASYNC_ERROR;
        } else {
            if (params[0].isEmpty() || params[1].isEmpty()) {
                Log.e(TAG, "Login or password is empty");
                this.errcode = BeeApplication.ASYNC_ERROR;
            } else {
                String pseudo = params[0];
                String password = params[1];
                String apisenseUrl = (params.length >= 3 && !params[2].isEmpty()) ?
                        params[2] : BeeApplication.BEE_DEFAULT_URL;
                try {
                    servService.createAccount(apisenseUrl, "", pseudo, password, "");
                    servService.connect(pseudo, password);
                    this.errcode = BeeApplication.ASYNC_SUCCESS;
                } catch (Exception e) {
                    details = e.getMessage();
                    APISLog.send(e, APISLog.ERROR);
                    this.errcode = BeeApplication.ASYNC_ERROR;
                }
            }
        }
        return details;
    }

}