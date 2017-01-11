package com.apisense.bee.callbacks;


import android.app.Activity;
import android.widget.Toast;

import com.apisense.bee.R;
import io.apisense.sdk.core.store.Crop;

public class OnCropUnsubscribed extends BeeAPSCallback<Crop> {
    private String cropName;

    public OnCropUnsubscribed(Activity activity, String cropName) {
        super(activity);
        this.cropName = cropName;
    }

    @Override
    public void onDone(Crop crop) {
        String toastMessage = String.format(activity.getString(R.string.experiment_unsubscribed), cropName);
        Toast.makeText(activity, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception e) {
        super.onError(e);
        String toastMessage = String.format("Error while unsubscribing from %s", cropName);
        Toast.makeText(activity, toastMessage, Toast.LENGTH_SHORT).show();
    }
}
