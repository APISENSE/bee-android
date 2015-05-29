package com.apisense.bee.backend;

import android.os.AsyncTask;

import com.apisense.bee.BeeApplication;

/**
 * Represents an asynchronous tasks that need postExecution dependent on the UI.
 * Here the 'Result' type represent the detailed output of the task (such as fetched Experiments, notifications, ...).
 * The returned status (Success or failure) is handled by the 'errcode' attribute.
 */

public abstract class AsyncTaskWithCallback<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private final String TAG = this.getClass().getSimpleName();
    protected int errcode = BeeApplication.ASYNC_UNSET_CODE;
    private AsyncTasksCallbacks listener;

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
