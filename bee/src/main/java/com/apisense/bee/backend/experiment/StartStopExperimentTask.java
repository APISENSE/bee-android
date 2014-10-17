package com.apisense.bee.backend.experiment;

import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Start and Stop AsyncTask wrapper to simplify usage in activities
 *
 */
public class StartStopExperimentTask {
    private String TAG = getClass().getSimpleName();

    public static final int EXPERIMENT_STARTED = 1;
    public static final int EXPERIMENT_STOPPED = 2;

    private final AsyncTasksCallbacks listener;

    // This task can either be a Start or a Stop Task
    private AsyncTaskWithCallback<Experiment, Void, Integer> task;

    public StartStopExperimentTask(AsyncTasksCallbacks listener){
        this.listener = listener;
    }

    public void execute(Experiment experiment) {
        if (! experiment.state) {
                Log.i(TAG, "Starting experiment: " + experiment);
                task = new StartExperimentTask(listener);
                task.execute(experiment);
        } else {
                Log.i(TAG, "Stopping experiment: " + experiment);
                task = new StopExperimentTask(listener);
                task.execute(experiment);
        }
    }

    private class StopExperimentTask extends AsyncTaskWithCallback<Experiment, Void, Integer> {
        private final String TAG = this.getClass().getSimpleName();

        public StopExperimentTask(AsyncTasksCallbacks listener) {
            super(listener);
        }

        @Override
        protected Integer doInBackground(Experiment... params) {
            Experiment exp = params[0];
            try {
                APISENSE.apisMobileService().stopExperiment(exp, APISENSE.EXIT_CODE_NORMAL);
                // TODO: Make {start,stop}Experiment change the given variable to avoid changing it by ourserlf.
                exp.state = false;
                Log.i(TAG, "Experiment (" + exp.name + ") stopped --- " + exp.state);
                this.errcode = BeeApplication.ASYNC_SUCCESS;
            } catch (Exception e) {
                Log.e(TAG, "Experiment (" + exp.name + ") stop failed: " + e.getMessage());
                this.errcode = BeeApplication.ASYNC_ERROR;
            }
            return EXPERIMENT_STOPPED;
        }
    }

    private class StartExperimentTask extends AsyncTaskWithCallback<Experiment, Void, Integer> {
        private final String TAG = this.getClass().getSimpleName();

        public StartExperimentTask(AsyncTasksCallbacks listener) {
            super(listener);
        }

        @Override
        protected Integer doInBackground(Experiment... params) {
            Experiment exp = params[0];
            try {
                APISENSE.apisMobileService().startExperiment(exp);
                // TODO: Make {start,stop}Experiment change the given variable to avoid changing it by ourserlf.
                exp.state = true;
                Log.i(TAG, "Experiment (" + exp.name + ") started");
                this.errcode = BeeApplication.ASYNC_SUCCESS;
            } catch (Exception e) {
                Log.w(TAG, "Experiment (" + exp.name + ") start failed: " + e.getMessage());
                this.errcode = BeeApplication.ASYNC_ERROR;
            }
            return EXPERIMENT_STARTED;
        }
    }
}
