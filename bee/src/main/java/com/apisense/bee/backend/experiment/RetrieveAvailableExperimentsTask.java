package com.apisense.bee.backend.experiment;

import android.content.Context;

import com.apisense.android.api.APS;
import com.apisense.android.api.APSCrop;
import com.apisense.android.api.APSRequest;
import com.apisense.core.api.Callback;
import com.apisense.core.api.Crop;
import com.apisense.core.api.Log;


import java.util.List;


/**
 * Task to fetch every experiment available to the user.
 *
 */
public class RetrieveAvailableExperimentsTask {
    private final String TAG = this.getClass().getSimpleName();

    private final Callback<List<Crop>> listener;
    private final Context context;

    public RetrieveAvailableExperimentsTask(Context context, Callback<List<Crop>> listener) {
        this.context = context;
        this.listener = listener;
    }

    public void execute(){
        execute("");
    }

    public void execute(String filter) {
        List<APSCrop> gotExperiments;
        Log.d(TAG, "Got Filter: " + filter);
        APSRequest<List<Crop>> request;
        try {
            request = APS.fetchCrop(context);
            request.setCallback(listener);
        } catch (Exception e) {
            Log.e(TAG, "Error while retrieving available experiments: " + e.getMessage());
        }
    }

}
