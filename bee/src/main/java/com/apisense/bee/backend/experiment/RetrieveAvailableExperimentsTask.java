package com.apisense.bee.backend.experiment;

import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BSenseServerService;

import java.util.ArrayList;
import java.util.List;

public class RetrieveAvailableExperimentsTask extends AsyncTaskWithCallback<Void, Void, List<Experiment>> {
    private final String TAG = this.getClass().getSimpleName();

    // Todo: Change types with API.
    private final static String RETURN_SIZE = "30";
    private String index = "0";

    public RetrieveAvailableExperimentsTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    public void setIndex(String index){
        this.index = index;
    }

    @Override
    protected List<Experiment> doInBackground(Void... params) {
        List<Experiment> gotExperiments;

        BSenseServerService servService = APISENSE.apisense().getBSenseServerService();
        try {
            servService.searchRemoteExperiment(index, RETURN_SIZE);
            gotExperiments = servService.getRemoteExperiments();
            this.errcode = BeeApplication.ASYNC_SUCCESS;
        } catch (Exception e) {
            Log.e(TAG, "Error while retrieving available experiments: " + e.getMessage());
            gotExperiments = new ArrayList<Experiment>();
            this.errcode = BeeApplication.ASYNC_ERROR;
        }
        Log.d(TAG, "List of experiments returned: " + gotExperiments.toString());
        return gotExperiments;
    }
}
