package com.apisense.bee.backend;

import android.os.AsyncTask;
import android.util.Log;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

import java.util.ArrayList;
import java.util.List;

public class RetrieveExperimentsTask extends AsyncTask<Void, Void, List<Experiment>>{
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTasksCallbacks listener;

    public RetrieveExperimentsTask(AsyncTasksCallbacks listener) {
        this.listener = listener;
    }

    @Override
    protected List<Experiment> doInBackground(Void... params) {
        List<Experiment> gotExperiments;
        if (!APISENSE.apisServerService().isConnected())
            gotExperiments = new ArrayList<Experiment>();
        else {
            gotExperiments = APISENSE.apisServerService().getRemoteExperiments();
        }
        return gotExperiments;
    }

    @Override
    protected void onPostExecute(final List<Experiment> response) {
        this.listener.onTaskCompleted(response);
    }

    @Override
    protected void onCancelled() {
        this.listener.onTaskCanceled();
    }
}
