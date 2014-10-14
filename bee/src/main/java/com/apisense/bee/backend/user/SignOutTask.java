package com.apisense.bee.backend.user;

import android.os.AsyncTask;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */

public class SignOutTask extends AsyncTask<String, Void, String> {
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTasksCallbacks listener;
    private final String DEFAULT_URL = "http://beta.apisense.io/hive";
    public SignOutTask(AsyncTasksCallbacks listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String returned = "success";
        try {
            APISENSE.apisMobileService().sendAllTrack();
            APISENSE.apisMobileService().stopAllExperiments(0);
            for(Experiment xp: APISENSE.apisMobileService().getInstalledExperiments().values())
                APISENSE.apisMobileService().uninstallExperiment(xp);
        } catch (Exception e) {
            e.printStackTrace();
            returned = "error";
        }
        APISENSE.apisServerService().disconnect();
         return returned;
    }

    @Override
    protected void onPostExecute(final String response) {
        this.listener.onTaskCompleted(response);
    }

    @Override
    protected void onCancelled() {
        this.listener.onTaskCanceled();
    }
}
