package com.apisense.bee.backend.experiment;

import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BSenseServerService;
import fr.inria.bsense.service.BeeSenseServiceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Task to fetch every experiment available to the user.
 *
 */
public class RetrieveAvailableExperimentsTask extends AsyncTaskWithCallback<String, Void, List<Experiment>> {
    private final String TAG = this.getClass().getSimpleName();

    // Todo: Change types with API.
    private final static String RETURN_SIZE = "300";
    private final BSenseServerService servService;

    private String index = "0";

    public RetrieveAvailableExperimentsTask(BeeSenseServiceManager apiService, AsyncTasksCallbacks listener) {
        super(listener);
        this.servService = apiService.getBSenseServerService();
    }

    public void setIndex(int index){
        this.index = Integer.toString(index);
    }

    @Override
    protected List<Experiment> doInBackground(String... params) {
        List<Experiment> gotExperiments;
        List<String> filters = Arrays.asList(params);
        Log.d(TAG, "Got Filters: " + filters);

        // TODO: Use filter to... filter
        try {
            servService.searchRemoteExperiment(index, RETURN_SIZE);
            gotExperiments = servService.getRemoteExperiments();
            gotExperiments = (gotExperiments != null)? gotExperiments : new ArrayList<Experiment>();
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
