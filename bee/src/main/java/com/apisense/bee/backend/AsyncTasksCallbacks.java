package com.apisense.bee.backend;


public interface AsyncTasksCallbacks {

    public void onTaskCompleted(final String response);
    public void onTaskCanceled();
}
