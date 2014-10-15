package com.apisense.bee.backend;

/**
 * This interface defines common callbacks for Asynchronous Tasks
 *
 */
public interface AsyncTasksCallbacks {

    /**
     * Method Called after completion of the task
     *
     * @param result One of the ASYNC_* constants of {@link com.apisense.bee.BeeApplication}
     * @param response The actual output of the {@link com.apisense.bee.backend.AsyncTaskWithCallback}
     */
    public void onTaskCompleted(final int result, final Object response);

    /**
     * Method called in case of the cancellation of the {@link com.apisense.bee.backend.AsyncTaskWithCallback}
     */
    public void onTaskCanceled();
}
