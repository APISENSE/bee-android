package com.apisense.bee.backend.user;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;

import fr.inria.apislog.APISLog;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BSenseMobileService;
import fr.inria.bsense.service.BSenseServerService;
import fr.inria.bsense.service.BeeSenseServiceManager;

/**
 * Represents an asynchronous Sign out task used to de-authenticate the user.
 */
public class SignOutTask extends AsyncTaskWithCallback<String, Void, String> {
    private final String TAG = this.getClass().getSimpleName();
    private final BSenseMobileService mobService;
    private final BSenseServerService servService;

    public SignOutTask(BeeSenseServiceManager apiServices, AsyncTasksCallbacks listener) {
        super(listener);
        mobService = apiServices.getBSenseMobileService();
        servService = apiServices.getBSenseServerService();
    }

    @Override
    protected String doInBackground(String... params) {
        this.errcode = BeeApplication.ASYNC_SUCCESS;
        String details = "";
        try {
            mobService.sendAllTrack();
            mobService.stopAllExperiments(0);
            for (Experiment xp : mobService.getInstalledExperiments().values())
                mobService.uninstallExperiment(xp);
        } catch (Exception e) {
            e.printStackTrace();
            details = e.getMessage();
            APISLog.send(e, APISLog.ERROR);
            this.errcode = BeeApplication.ASYNC_SUCCESS;
        }
        servService.disconnect();
        return details;
    }
}
