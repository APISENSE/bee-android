package com.apisense.bee.backend.experiment;

import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BSenseMobileService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Task to fetch every experiment installed (i.e. subscribed) by the user.
 *
 */
public class RetrieveInstalledExperimentsTask extends AsyncTaskWithCallback<Void, Void, List<Experiment>> {
    private final String TAG = this.getClass().getSimpleName();

    public RetrieveInstalledExperimentsTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected List<Experiment> doInBackground(Void... params) {
        List<Experiment> gotExperiments;

        if (! APISENSE.apisServerService().isConnected()){
            // Todo: Specific treatment for anonymous user?
            gotExperiments = new ArrayList<Experiment>();
        }else {
            // Only retrieve installed experiments
            BSenseMobileService mobService = APISENSE.apisense().getBSenseMobileService();
            Collection exp = mobService.getInstalledExperiments().values();
            gotExperiments = (exp instanceof List) ? (List) exp : new ArrayList(exp);
            this.errcode = BeeApplication.ASYNC_SUCCESS;
        }
        Log.d(TAG, "List of experiments returned: " + gotExperiments.toString());
        return gotExperiments;
    }
}