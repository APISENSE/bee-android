package com.apisense.bee.backend;


public interface AsyncTasksCallbacks {

    public void onTaskCompleted(final Object response, final String details);
    public void onTaskCanceled();
}
