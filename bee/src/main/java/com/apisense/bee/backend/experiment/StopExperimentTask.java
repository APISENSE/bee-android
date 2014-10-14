package com.apisense.bee.backend.experiment;

import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

public class StopExperimentTask extends AsyncTaskWithCallback<Experiment, Void, Void> {
    private final String TAG = this.getClass().getSimpleName();

    public StopExperimentTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected Void doInBackground(Experiment... params) {
        Experiment exp = params[0];
        try {
            // TODO: Change use of exitcode (0) with API constant.
            APISENSE.apisense().getBSenseMobileService().stopExperiment(exp, 0);
            Log.i(TAG, "Experiment (" + exp.name + ") stopped");
            this.errcode = BeeApplication.ASYNC_SUCCESS;
        } catch (Exception e) {
            Log.e(TAG, "Experiment (" + exp.name + ") stop failed: " + e.getMessage());
            this.errcode = BeeApplication.ASYNC_ERROR;
        }
        return null;
    }
}
