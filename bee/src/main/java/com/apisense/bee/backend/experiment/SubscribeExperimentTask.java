package com.apisense.bee.backend.experiment;

import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Task to subscribe to and install the given experiment
 *
 */
public class SubscribeExperimentTask extends AsyncTaskWithCallback<Experiment, Void, String>{
    private final String TAG = this.getClass().getSimpleName();

    public SubscribeExperimentTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(Experiment... params) {
        Experiment exp = params[0];
        String detail = "";
        if (exp != null) {
            try {
                APISENSE.apisServerService().subscribeExperiment(exp);
                APISENSE.apisMobileService().installExperiment(exp);
                Log.i(TAG, "Subscribe experiment " + exp.name);
            } catch (Exception e) {
                Log.e(TAG, "Error  on subscribe experiment " + exp.name + " | Error=" + e.getMessage());
                this.errcode = BeeApplication.ASYNC_ERROR;
                detail = e.getMessage();
            }
        }
        this.errcode = BeeApplication.ASYNC_SUCCESS;
        return detail;
    }
}