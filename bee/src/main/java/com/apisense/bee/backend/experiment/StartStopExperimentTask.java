package com.apisense.bee.backend.experiment;

import android.util.Log;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;

import fr.inria.apislog.APISLog;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BSenseMobileService;
import fr.inria.bsense.service.BeeSenseServiceManager;

/**
 * Start and Stop AsyncTask wrapper to simplify usage in activities
 */
public class StartStopExperimentTask {
    public static final int EXPERIMENT_STARTED = 1;
    public static final int EXPERIMENT_STOPPED = 2;
    private final AsyncTasksCallbacks listener;
    private final BSenseMobileService mobServices;
    private String TAG = getClass().getSimpleName();
    private APISLog mAPISLog;

    // This task can either be a Start or a Stop Task
    private AsyncTaskWithCallback<Experiment, Void, Integer> task;

    public StartStopExperimentTask(BeeSenseServiceManager apiServices, AsyncTasksCallbacks listener) {
        this.listener = listener;
        this.mobServices = apiServices.getBSenseMobileService();
    }

    public void execute(Experiment experiment) {
        if (!experiment.state) {
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
                mobServices.stopExperiment(exp, APISENSE.EXIT_CODE_NORMAL);
                // TODO: Make {start,stop}Experiment change the given variable to avoid changing it by ourserlf.
                exp.state = false;
                Log.i(TAG, "Experiment (" + exp.name + ") stopped --- " + exp.state);
                this.errcode = BeeApplication.ASYNC_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Experiment (" + exp.name + ") stop failed: " + e.getMessage());
                APISLog.send(e, APISLog.WARNING);
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
                mobServices.startExperiment(exp);
                // TODO: Make {start,stop}Experiment change the given variable to avoid changing it by ourserlf.
                exp.state = true;
                Log.i(TAG, "Experiment (" + exp.name + ") started");
                this.errcode = BeeApplication.ASYNC_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                Log.w(TAG, "Experiment (" + exp.name + ") start failed: " + e.getMessage());
                APISLog.send(e, APISLog.WARNING);
                this.errcode = BeeApplication.ASYNC_ERROR;
            }
            return EXPERIMENT_STARTED;
        }
    }
}
