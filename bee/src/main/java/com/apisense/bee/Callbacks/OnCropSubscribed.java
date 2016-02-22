package com.apisense.bee.Callbacks;

import android.app.Activity;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.bee.utils.CropPermissionHandler;
import com.apisense.sdk.core.store.Crop;
import com.rollbar.android.Rollbar;


public class OnCropSubscribed extends BeeAPSCallback<Crop> {
    private Activity context;
    private Crop crop;
    private CropPermissionHandler permissionHandler;

    public OnCropSubscribed(Activity context, Crop crop, CropPermissionHandler permissionHandler) {
        super(context);
        this.context = context;
        this.crop = crop;
        this.permissionHandler = permissionHandler;
    }

    @Override
    public void onDone(Crop crop) {
        String toastMessage = String.format(context.getString(R.string.experiment_subscribed), crop.getName());
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        permissionHandler.startOrRequestPermissions();
    }

    @Override
    public void onError(Exception e) {
        super.onError(e);
        String toastMessage = String.format("Error while subscribing to %s", crop.getName());
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        Rollbar.reportException(e);
    }
}
