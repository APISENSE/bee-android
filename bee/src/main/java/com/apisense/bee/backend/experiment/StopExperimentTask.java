package com.apisense.bee.backend.experiment;

import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Task to stop gathering data for the given experiment
 *
 */
public class StopExperimentTask extends AsyncTaskWithCallback<Experiment, Void, Void> {
    private final String TAG = this.getClass().getSimpleName();

    public StopExperimentTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected Void doInBackground(Experiment... params) {
        Experiment exp = params[0];
        try {
            APISENSE.apisMobileService().stopExperiment(exp, APISENSE.EXIT_CODE_NORMAL);
            Log.i(TAG, "Experiment (" + exp.name + ") stopped");
            // FIXME: find a way to delete me!
            exp.state = false;
            this.errcode = BeeApplication.ASYNC_SUCCESS;
        } catch (Exception e) {
            Log.e(TAG, "Experiment (" + exp.name + ") stop failed: " + e.getMessage());
            this.errcode = BeeApplication.ASYNC_ERROR;
        }
        return null;
    }
}
