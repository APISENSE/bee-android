package com.apisense.bee.backend.experiment;

import android.util.Log;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import fr.inria.apislog.APISLog;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BSenseMobileService;
import fr.inria.bsense.service.BSenseServerService;
import fr.inria.bsense.service.BeeSenseServiceManager;

/**
 * Task to fetch every experiment installed (i.e. subscribed) by the user.
 */
public class RetrieveInstalledExperimentsTask extends AsyncTaskWithCallback<Void, Void, List<Experiment>> {
    private final String TAG = this.getClass().getSimpleName();

    private final BSenseMobileService mobService;
    private final BSenseServerService servService;

    public RetrieveInstalledExperimentsTask(BeeSenseServiceManager apiService, AsyncTasksCallbacks listener) {
        super(listener);
        mobService = apiService.getBSenseMobileService();
        servService = apiService.getBSenseServerService();
    }

    @Override
    protected List<Experiment> doInBackground(Void... params) {
        List<Experiment> returnedExperiments = new ArrayList<Experiment>();
        ;

        // Only retrieve installed experiments
        Map<String, Experiment> gotExperiments = mobService.getInstalledExperiments();
        try {
            Collection exp = gotExperiments.values();
            returnedExperiments = (exp instanceof List) ? (List) exp : new ArrayList(exp);
        } catch (NullPointerException e) {
            APISLog.send(e, APISLog.ERROR);
        }
        this.errcode = BeeApplication.ASYNC_SUCCESS;

        Log.d(TAG, "List of experiments returned: " + returnedExperiments.toString());
        return returnedExperiments;
    }
}