package com.apisense.bee.Callbacks;


import android.content.Context;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.sdk.core.store.Crop;
import com.rollbar.android.Rollbar;

public class OnCropUnsubscribed extends BeeAPSCallback<Crop> {
    private Context context;
    private String cropName;

    public OnCropUnsubscribed(Context context, String cropName) {
        super(context);
        this.context = context;
        this.cropName = cropName;
    }

    @Override
    public void onDone(Crop crop) {
        String toastMessage = String.format(context.getString(R.string.experiment_unsubscribed), cropName);
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception e) {
        super.onError(e);
        String toastMessage = String.format("Error while unsubscribing from %s", cropName);
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        Rollbar.reportException(e);
    }
}
