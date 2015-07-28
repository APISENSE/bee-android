package com.apisense.bee.Callbacks;

import android.content.Context;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.sdk.core.APSCallback;

public class OnCropStarted implements APSCallback<Void> {

    private Context context;
    private String cropName;

    public OnCropStarted(Context context, String cropName) {
        this.context = context;
        this.cropName = cropName;
    }

    @Override
    public void onDone(Void aVoid) {
        String message = String.format(context.getString(R.string.experiment_started), cropName);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(context, "Error on start (" + e.getLocalizedMessage() + ")", Toast.LENGTH_SHORT).show();
    }
}
