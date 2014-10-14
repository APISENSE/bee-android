package com.apisense.bee.backend.user;

import android.os.AsyncTask;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */

public class SignOutTask extends AsyncTaskWithCallback {
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTasksCallbacks listener;

    public SignOutTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected Integer doInBackground(String... params) {
        Integer returned = BeeApplication.ASYNC_SUCCESS;
        try {
            APISENSE.apisMobileService().sendAllTrack();
            APISENSE.apisMobileService().stopAllExperiments(0);
            for(Experiment xp: APISENSE.apisMobileService().getInstalledExperiments().values())
                APISENSE.apisMobileService().uninstallExperiment(xp);
        } catch (Exception e) {
            e.printStackTrace();
            returned = BeeApplication.ASYNC_SUCCESS;
        }
        APISENSE.apisServerService().disconnect();
        return returned;
    }
}
