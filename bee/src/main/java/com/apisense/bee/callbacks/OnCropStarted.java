package com.apisense.bee.callbacks;

import android.content.Context;
import android.widget.Toast;

import com.apisense.bee.R;
import io.apisense.sdk.core.APSCallback;
import io.apisense.sdk.core.store.Crop;

public class OnCropStarted implements APSCallback<Crop> {
    private Context context;

    public OnCropStarted(Context context) {
        this.context = context;
    }

    @Override
    public void onDone(Crop crop) {
        String message = String.format(context.getString(R.string.experiment_started), crop.getName());
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(context, "Error on start (" + e.getLocalizedMessage() + ")", Toast.LENGTH_SHORT).show();
    }
}
