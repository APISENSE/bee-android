package com.apisense.bee.backend;

import android.os.AsyncTask;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Represents an asynchronous tasks that need postExecution dependent of the UI.
 * Here the 'Result' type represent the detailed output of the task (such as fetched Experiments, notifications, ...)
 * The returned status (Success or failure) is handled by the 'errcode' attribute.
 *
 */

public abstract class AsyncTaskWithCallback<Params, Progress, Result> extends AsyncTask <Params, Progress, Result> {
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTasksCallbacks listener;
    protected int errcode = BeeApplication.ASYNC_UNSET_CODE;

    public AsyncTaskWithCallback(AsyncTasksCallbacks listener) {
        this.listener = listener;
    }

     @Override
    protected void onPostExecute(final Result response) {
        this.listener.onTaskCompleted(this.errcode, response);
    }

    @Override
    protected void onCancelled() {
        this.listener.onTaskCanceled();
    }
}
