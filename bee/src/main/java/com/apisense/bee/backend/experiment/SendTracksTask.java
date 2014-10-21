package com.apisense.bee.backend.experiment;

import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BSenseMobileService;
import fr.inria.bsense.service.BeeSenseServiceManager;

/**
 *   AsyncTask used to force the sent of Tracks for a given experiment
 *
 */
public class SendTracksTask extends AsyncTaskWithCallback<Experiment, Void, Void>{
    private final String TAG = this.getClass().getSimpleName();
    private final BSenseMobileService mobService;

    public SendTracksTask(BeeSenseServiceManager apiServices, AsyncTasksCallbacks listener) {
        super(listener);
        this.mobService = apiServices.getBSenseMobileService();
    }

    @Override
    protected Void doInBackground(Experiment... params) {
        if (params.length <= 0) {
            this.errcode = BeeApplication.ASYNC_ERROR;
            Log.w(TAG, "No experiment given to SendTrackTask, nothing done" );
        } else {
            Experiment exp = params[0];
            try {
                // FIXME: sendTrack is safe, why does it throws Exception?
                mobService.sendTrack(exp);
                Log.i(TAG, "Tracks sent for experiment:" + exp.name);
                this.errcode = BeeApplication.ASYNC_SUCCESS;
            } catch (Exception e) {
                Log.w(TAG, "Experiment (" + exp.name + ") failed to send tracks: " + e.getMessage());
                this.errcode = BeeApplication.ASYNC_ERROR;
            }
        }
        return null;
    }
}
