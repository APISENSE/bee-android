package com.apisense.bee.callbacks;

import android.app.Activity;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.bee.utils.CropPermissionHandler;
import io.apisense.sdk.core.store.Crop;


public class OnCropSubscribed extends BeeAPSCallback<Crop> {
    private Crop crop;
    private CropPermissionHandler permissionHandler;

    public OnCropSubscribed(Activity activity, Crop crop, CropPermissionHandler permissionHandler) {
        super(activity);
        this.crop = crop;
        this.permissionHandler = permissionHandler;
    }

    @Override
    public void onDone(Crop crop) {
        String toastMessage = String.format(activity.getString(R.string.experiment_subscribed), crop.getName());
        Toast.makeText(activity, toastMessage, Toast.LENGTH_SHORT).show();
        permissionHandler.startOrRequestPermissions();
    }

    @Override
    public void onError(Exception e) {
        super.onError(e);
        String toastMessage = String.format("Error while subscribing to %s", crop.getName());
        Toast.makeText(activity, toastMessage, Toast.LENGTH_SHORT).show();
    }
}
