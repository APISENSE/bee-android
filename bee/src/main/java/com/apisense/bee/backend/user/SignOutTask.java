package com.apisense.bee.backend.user;

import android.os.AsyncTask;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Represents an asynchronous Sign out task used to de-authenticate the user.
 */

public class SignOutTask extends AsyncTaskWithCallback<String, Void, String> {
    private final String TAG = this.getClass().getSimpleName();

    public SignOutTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... params) {
        this.errcode = BeeApplication.ASYNC_SUCCESS;
        String details = "";
        try {
            APISENSE.apisMobileService().sendAllTrack();
            APISENSE.apisMobileService().stopAllExperiments(0);
            for(Experiment xp: APISENSE.apisMobileService().getInstalledExperiments().values())
                APISENSE.apisMobileService().uninstallExperiment(xp);
        } catch (Exception e) {
            e.printStackTrace();
            details = e.getMessage();
            this.errcode = BeeApplication.ASYNC_SUCCESS;
        }
        APISENSE.apisServerService().disconnect();
        return details;
    }
}
