package com.apisense.bee.backend;


public interface AsyncTasksCallbacks {

    public void onTaskCompleted(final int result, final Object response);
    public void onTaskCanceled();
}
