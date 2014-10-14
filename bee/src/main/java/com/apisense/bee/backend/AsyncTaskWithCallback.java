package com.apisense.bee.backend;

import android.os.AsyncTask;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */

// TODO: no restriction on Input and output, possible?
public abstract class AsyncTaskWithCallback extends AsyncTask<String, Void, Integer> {
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTasksCallbacks listener;
    protected String details = "";

    public AsyncTaskWithCallback(AsyncTasksCallbacks listener) {
        this.listener = listener;
    }

     @Override
    protected void onPostExecute(final Integer response) {
        this.listener.onTaskCompleted(response, this.details);
    }

    @Override
    protected void onCancelled() {
        this.listener.onTaskCanceled();
    }
}
