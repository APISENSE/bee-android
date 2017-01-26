package com.apisense.bee.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.apisense.bee.BeeApplication;
import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.APSCallback;
import io.apisense.sdk.core.store.Crop;

import java.util.Set;

public class CropPermissionHandler {
    private static final int REQUEST_PERMISSION_START_CROP = 1;
    private final APISENSE.Sdk apisenseSdk;
    private Activity source;
    private final Crop crop;
    private APSCallback<Crop> callback;

    public CropPermissionHandler(Activity source, Crop crop, APSCallback<Crop> callback) {
        this.callback = callback;
        this.apisenseSdk = ((BeeApplication) source.getApplication()).getSdk();
        this.source = source;
        this.crop = crop;
    }

    /**
     * Check for needed permissions, and request them if some are denied.
     * (The crop start will then be managed in
     * {@link CropPermissionHandler#onRequestPermissionsResult(int, String[], int[])})
     *
     * If every permissions are granted, start the crop.
     */
    public void startOrRequestPermissions() {
        Set<String> deniedPermissions = apisenseSdk.getCropManager().deniedPermissions(crop);
        if (deniedPermissions.isEmpty()) {
            apisenseSdk.getCropManager().start(crop, callback);
        } else {
            ActivityCompat.requestPermissions(source,
                    deniedPermissions.toArray(new String[deniedPermissions.size()]),
                    REQUEST_PERMISSION_START_CROP
            );
        }
    }

    /**
     * Contains the common behavior for a permission request callback about a crop.
     *
     * @param requestCode The request code.
     * @param permissions The asked permissions.
     * @param grantResults The grant result.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_START_CROP) {
            if (permissionsGranted(grantResults)) {
                apisenseSdk.getCropManager().start(crop, callback);
            }
        }
    }

    /**
     * Tells if every asked permissions has been granted.
     *
     * @param grantResults The permissions grant status.
     * @return True if every permissions are granted, false otherwise.
     */
    private boolean permissionsGranted(@NonNull int[] grantResults) {
        boolean everythingGranted = true;
        for (int grantResult : grantResults) {
            everythingGranted = everythingGranted && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        return everythingGranted;
    }
}

