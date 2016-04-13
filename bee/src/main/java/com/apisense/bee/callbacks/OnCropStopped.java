package com.apisense.bee.callbacks;

import android.content.Context;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.store.Crop;

public class OnCropStopped implements APSCallback<Crop> {

    private Context context;

    public OnCropStopped(Context context) {
        this.context = context;
    }

    @Override
    public void onDone(Crop crop) {
        String message = String.format(context.getString(R.string.experiment_stopped), crop.getName());
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(context, "Error on stop (" + e.getLocalizedMessage() + ")", Toast.LENGTH_SHORT).show();
    }
}
