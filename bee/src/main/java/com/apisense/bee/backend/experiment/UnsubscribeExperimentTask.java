package com.apisense.bee.backend.experiment;

import android.os.AsyncTask;
import android.util.Log;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * AsyncTask to stop and unsubscribe an experiment
 *
 * @author Christophe Ribeiro <christophe.ribeiro@inria.fr>
 *
 */
public class UnsubscribeExperimentTask extends AsyncTask<Experiment, Void, Boolean> {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected Boolean doInBackground(Experiment... params) {
        Experiment exp = params[0];
        if (exp != null && exp.state)
            try {
                APISENSE.apisMobileService().stopExperiment(exp, 0);
                Log.i(TAG, "Stop experiment  " + exp.name);
            } catch (Exception e) {
                Log.e(TAG, "Error stop experiment " + exp.name + " | Error=" + e.getMessage());
                return false;
            }
        try {
            APISENSE.apisServerService().unsubscribeExperiment(exp);
            Log.i(TAG, "Unsubscribe experiment " + exp.name);
        } catch (Exception e1) {
            Log.e(TAG, "Error unsubscribe experiment " + exp.name + " | Error=" + e1.getMessage());
            return false;
        }
        return true;
    }
}