package com.apisense.bee.backend.experiment;

import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/*
 *   AsyncTask used to fetch the notifications about a given experiment
 */
public class RetrieveNotificationsTask extends AsyncTaskWithCallback<Experiment, Void, Object> {
    private final String TAG = this.getClass().getSimpleName();

    public RetrieveNotificationsTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected Object doInBackground(Experiment... params) {
        // TODO: Implemented in da future
        return null;
    }
}
