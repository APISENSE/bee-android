package com.apisense.bee.backend.experiment;

import android.content.Context;
import android.util.Log;
import com.apisense.android.api.APS;
import com.apisense.api.Callback;
import java.util.ArrayList;
import java.util.List;

/**
 * Subscribe and Unsubscribe AsyncTask wrapper to simplify usage in activities
 *
 */
public class SubscribeUnsubscribeExperimentTask {
    private String TAG = getClass().getSimpleName();

    public static final int EXPERIMENT_SUBSCRIBED = 1;
    public static final int EXPERIMENT_UNSUBSCRIBED = 2;

    private Context context;
    private final Callback<Integer> listener;

    public SubscribeUnsubscribeExperimentTask(Context context, Callback<Integer> listener){
        this.context = context;
        this.listener = listener;
    }

    public void execute(String cropId) {
        try {
            if (isInstalled(context, cropId)) {
                Log.i(TAG, "Asking un-subscription to experiment: " + cropId);
                // TODO: Release uninstallCropMethod
//                APS.uninstallCrop(context, cropId);
                listener.onCall(EXPERIMENT_UNSUBSCRIBED);
            } else {
                Log.i(TAG, "Asking subscription to experiment: " + cropId);
                APS.installCrop(context, cropId);
                listener.onCall(EXPERIMENT_SUBSCRIBED);
           }
        } catch (Exception e){
            e.printStackTrace();
            listener.onError(e);
        }
    }

    /**
     * Specify if the given experiment is already subscribed to
     * (*subscribed* being currently equivalent to *installed*)
     *
     * @param cropId The id of the Crop to create
     * @return true if the user already subscribed to an experiment, false otherwise
     */
    public static boolean isInstalled(Context context, String cropId) {
        List<String> installedCrops = new ArrayList<String>();
        try {
            installedCrops = APS.getInstalledCrop(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return installedCrops.contains(cropId);
    }
}
