package com.apisense.bee.backend;


public interface AsyncTasksCallbacks {

    public void onTaskCompleted(final Object response);
    public void onTaskCanceled();
}
