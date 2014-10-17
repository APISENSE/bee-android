package com.apisense.bee.backend.experiment;

import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;
import org.json.JSONException;

/**
 * Subscribe and Unsubscribe AsyncTask wrapper to simplify usage in activities
 *
 */
public class SubscribeUnsubscribeExperimentTask {
    private String TAG = getClass().getSimpleName();

    public static final int EXPERIMENT_SUBSCRIBED = 1;
    public static final int EXPERIMENT_UNSUBSCRIBED = 2;

    private final AsyncTasksCallbacks listener;

    // This task can either be a Subscribe or a Unsubscribe Task
    private AsyncTaskWithCallback<Experiment, Void, Integer> task;

    public SubscribeUnsubscribeExperimentTask(AsyncTasksCallbacks listener){
        this.listener = listener;
    }

    public void execute(Experiment experiment) {
        if (isSubscribedExperiment(experiment)) {
            Log.i(TAG, "Asking un-subscription to experiment: " + experiment);
            task = new UnsubscribeExperimentTask(listener);
            task.execute(experiment);
        } else {
            Log.i(TAG, "Asking subscription to experiment: " + experiment);
            task = new SubscribeExperimentTask(listener);
            task.execute(experiment);
        }
    }

    /**
     * Specify if the given experiment is already subscribed to
     * (*subscribed* being currently equivalent to *installed*)
     *
     * @param exp The experiment to test
     * @return true if the user already subscribed to an experiment, false otherwise
     */
    // TODO: Improve this with a specific library method (a field in Experiment would be even better)
    public static boolean isSubscribedExperiment(Experiment exp) {
        Experiment currentExperiment;
        boolean result = false;
        try {
            currentExperiment = APISENSE.apisense().getBSenseMobileService().getExperiment(exp.name);
            result = (currentExperiment != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

        /**
         * Task to subscribe to and install the given experiment
         *
         */
    public class SubscribeExperimentTask extends AsyncTaskWithCallback<Experiment, Void, Integer>{
        private final String TAG = this.getClass().getSimpleName();

        public SubscribeExperimentTask(AsyncTasksCallbacks listener) {
            super(listener);
        }

        @Override
        protected Integer doInBackground(Experiment... params) {
            Experiment exp = params[0];
            String detail = "";
            if (exp != null) {
                try {
                    APISENSE.apisServerService().subscribeExperiment(exp);
                    APISENSE.apisMobileService().installExperiment(exp);
                    Log.i(TAG, "Subscribe experiment " + exp.name);
                } catch (Exception e) {
                    Log.e(TAG, "Error  on subscribe experiment " + exp.name + " | Error=" + e.getMessage());
                    this.errcode = BeeApplication.ASYNC_ERROR;
                    detail = e.getMessage();
                }
            }
            this.errcode = BeeApplication.ASYNC_SUCCESS;
            return EXPERIMENT_SUBSCRIBED;
        }
    }

    /**
     * AsyncTask to stop, uninstall and unsubscribe from an experiment
     *
     */
    private class UnsubscribeExperimentTask extends AsyncTaskWithCallback<Experiment, Void, Integer> {

        private final String TAG = this.getClass().getSimpleName();

        public UnsubscribeExperimentTask(AsyncTasksCallbacks listener) {
            super(listener);
        }

        @Override
        protected Integer doInBackground(Experiment... params) {
            Experiment exp = params[0];
            String detail = "";

            // Retrieve local version of the experiment
            try {
                exp = APISENSE.apisMobileService().getExperiment(exp.name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Stop experiment if started
            if (exp != null && exp.state) {
                try {
                    APISENSE.apisMobileService().stopExperiment(exp, 0);
                    Log.i(TAG, "Stop experiment  " + exp.name);
                } catch (Exception e) {
                    Log.e(TAG, "Error stop experiment " + exp.name + " | Error=" + e.getMessage());
                    detail = e.getMessage();
                }
            }

            // Uninstall and unsubscribe from experiment
            try {
                APISENSE.apisMobileService().uninstallExperiment(exp);
                APISENSE.apisServerService().unsubscribeExperiment(exp);
                Log.i(TAG, "Unsubscribe experiment " + exp.name);
                this.errcode = BeeApplication.ASYNC_SUCCESS;
            } catch (Exception e) {
                Log.e(TAG, "Error unsubscribe experiment " + exp.name + " | Error=" + e.getMessage());
                detail = e.getMessage();
                this.errcode = BeeApplication.ASYNC_ERROR;
            }
            return EXPERIMENT_UNSUBSCRIBED;
        }
    }
}
