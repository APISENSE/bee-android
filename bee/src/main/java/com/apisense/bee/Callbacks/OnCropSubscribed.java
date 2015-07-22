package com.apisense.bee.Callbacks;

import android.content.Context;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.sdk.core.APSCallback;
import com.rollbar.android.Rollbar;

public class OnCropSubscribed implements APSCallback<Void> {
    private Context context;
    private String cropName;

    public OnCropSubscribed(Context context, String cropName) {
        this.context = context;
        this.cropName = cropName;
    }

    @Override
    public void onDone(Void response) {
        String toastMessage = String.format(context.getString(R.string.experiment_subscribed), cropName);
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception e) {
        String toastMessage = String.format("Error while subscribing to %s", cropName);
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        Rollbar.reportException(e);
    }
 }
