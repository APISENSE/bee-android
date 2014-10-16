package com.apisense.bee.backend.experiment;

import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;
import org.json.JSONException;

/**
 * AsyncTask to stop, uninstall and unsubscribe from an experiment
 *
 * @author Christophe Ribeiro <christophe.ribeiro@inria.fr>
 *
 */
public class UnsubscribeExperimentTask extends AsyncTaskWithCallback<Experiment, Void, String> {

    private final String TAG = this.getClass().getSimpleName();

    public UnsubscribeExperimentTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(Experiment... params) {
        Experiment exp = params[0];
        String detail = "";

        // Retrieve local version of the experiment
        try {
            exp = APISENSE.apisMobileService().getExperiment(exp.name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Stop experiment if started
        if (exp != null && exp.state) {
            try {
                APISENSE.apisMobileService().stopExperiment(exp, 0);
                Log.i(TAG, "Stop experiment  " + exp.name);
            } catch (Exception e) {
                Log.e(TAG, "Error stop experiment " + exp.name + " | Error=" + e.getMessage());
                detail = e.getMessage();
            }
        }

        // Uninstall and unsubscribe from experiment
        try {
            APISENSE.apisMobileService().uninstallExperiment(exp);
            APISENSE.apisServerService().unsubscribeExperiment(exp);
            Log.i(TAG, "Unsubscribe experiment " + exp.name);
            this.errcode = BeeApplication.ASYNC_SUCCESS;
        } catch (Exception e) {
            Log.e(TAG, "Error unsubscribe experiment " + exp.name + " | Error=" + e.getMessage());
            detail = e.getMessage();
            this.errcode = BeeApplication.ASYNC_ERROR;
        }
        return detail;
    }
}