package com.apisense.bee.backend.experiment;

import android.os.AsyncTask;
import android.util.Log;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BSenseMobileService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RetrieveExperimentsTask extends AsyncTask<Void, Void, List<Experiment>>{
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTasksCallbacks listener;

    public final static int GET_INSTALLED_EXPERIMENTS = 0;
    public final static int GET_REMOTE_EXPERIMENTS = 1;

    private int retrievalType;
    private String details = "";

    public RetrieveExperimentsTask(AsyncTasksCallbacks listener, int retrievalType) {
        this.listener = listener;
        this.retrievalType = retrievalType;
    }

    @Override
    protected List<Experiment> doInBackground(Void... params) {
        List<Experiment> gotExperiments;

        if (! APISENSE.apisServerService().isConnected()){
            // Specific treatment for anonymous user?
            gotExperiments = new ArrayList<Experiment>();
        }else {
            switch (retrievalType) {
                case GET_INSTALLED_EXPERIMENTS:
                    BSenseMobileService mobService = APISENSE.apisense().getBSenseMobileService();
                    Collection exp = mobService.getInstalledExperiments().values();
                    gotExperiments = (exp instanceof List) ? (List) exp : new ArrayList(exp);
                    break;
                case GET_REMOTE_EXPERIMENTS:
                    gotExperiments = APISENSE.apisServerService().getRemoteExperiments();
                    break;
                default:
                    gotExperiments = new ArrayList<Experiment>();
            }
        }
        Log.d(TAG, "List of experiments returned: " + gotExperiments.toString());
        return gotExperiments;
    }


    @Override
    protected void onPostExecute(final List<Experiment> response) {
        this.listener.onTaskCompleted(response, details);
    }

    @Override
    protected void onCancelled() {
        this.listener.onTaskCanceled();
    }
}
