package com.apisense.bee.backend.experiment;

import android.content.Context;
import android.util.Log;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.android.api.APSRequest;
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
    private Callback<APSLocalCrop> onSubscribedListener;
    private Callback<Void> onUnSubscribedlistener;

    public SubscribeUnsubscribeExperimentTask(Context context,
                                              Callback<APSLocalCrop> onSubscribedListener,
                                              Callback<Void> onUnSubscribedListener){
        this.context = context;
        this.onSubscribedListener = onSubscribedListener;
        this.onUnSubscribedlistener = onUnSubscribedListener;
    }

    public void execute(String cropId) {
        try {
            if (isInstalled(context, cropId)) {
                Log.i(TAG, "Asking un-subscription to experiment: " + cropId);
                APSRequest<Void> request = APS.uninstallCrop(context, cropId);
                // FIXME: Callback not called back!
                request.setCallback(onUnSubscribedlistener);
                onUnSubscribedlistener.onCall(null);
            } else {
                Log.i(TAG, "Asking subscription to experiment: " + cropId);
                APSRequest<APSLocalCrop> request = APS.installCrop(context, cropId);
                // FIXME: Callback not called back!
                request.setCallback(onSubscribedListener);
                onSubscribedListener.onCall(new APSLocalCrop("{}".getBytes()));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Specify if the given experiment is already subscribed to
     * (*subscribed* being currently equivalent to *installed*)
     *
     * @param cropId The id of the Crop to create
     * @return true if the user already subscribed to an experiment, false otherwise
     */
    // FIXME : change isInstalled method (lots of request on it), set in SDK
    public static boolean isInstalled(Context context, String cropId) {
        List<String> installedCrops = new ArrayList<String>();
        try {
           installedCrops = APS.getInstalledCrop(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("SubscribeUnsubscribeExperimentTask", "Got installed crops: " + installedCrops);
        return installedCrops.contains(cropId);
    }
}
