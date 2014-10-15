package com.apisense.bee.backend.experiment;

import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 *   AsyncTask used to force the sent of Tracks for a given experiment
 *
 */
public class SendTracksTask extends AsyncTaskWithCallback<Experiment, Void, Void>{
    private final String TAG = this.getClass().getSimpleName();

    public SendTracksTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected Void doInBackground(Experiment... params) {
        Experiment exp = params[0];
        try {
            APISENSE.apisense().getBSenseMobileService().sendTrack(exp);
            Log.i(TAG, "Tracks sent for experiment:" + exp.name);
            this.errcode = BeeApplication.ASYNC_SUCCESS;
        } catch (Exception e) {
            Log.w(TAG, "Experiment (" + exp.name + ") failed to send tracks: " + e.getMessage());
            this.errcode = BeeApplication.ASYNC_ERROR;
        }
        return null;
    }
}
