package com.apisense.bee.Callbacks;

import android.content.Context;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.store.Crop;
import com.rollbar.android.Rollbar;

public class OnCropSubscribed implements APSCallback<Crop> {
    private Context context;
    private Crop crop;
    private APISENSE.Sdk sdk;

    public OnCropSubscribed(Context context, Crop crop, APISENSE.Sdk sdk) {
        this.context = context;
        this.crop = crop;
        this.sdk = sdk;
    }

    @Override
    public void onDone(Crop crop) {
        String toastMessage = String.format(context.getString(R.string.experiment_subscribed), crop.getName());
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        sdk.getCropManager().start(crop, new OnCropStarted(context));
    }

    @Override
    public void onError(Exception e) {
        String toastMessage = String.format("Error while subscribing to %s", crop.getName());
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        Rollbar.reportException(e);
    }
 }
