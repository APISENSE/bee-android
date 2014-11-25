package com.apisense.bee.backend.experiment;

import android.content.Context;
import android.util.Log;
import com.apisense.android.api.APS;
import com.apisense.api.Callback;
import com.apisense.api.LocalCrop;

import java.util.ArrayList;
import java.util.List;

/**
 * Task to fetch every experiment installed (i.e. subscribed) by the user.
 *
 */
public class RetrieveInstalledExperimentsTask {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private Callback<List<LocalCrop>> listener;

    public RetrieveInstalledExperimentsTask(Context context, Callback<List<LocalCrop>> listener) {
        this.context = context;
        this.listener = listener;
    }

    public void execute() {
        try {
            List<String> returnIds = APS.getInstalledCrop(context);
            for (String exp: returnIds) {
                Log.i(TAG, exp);
            }
            List<LocalCrop> gotCrops =retrieveLocalCrops(returnIds);
            this.listener.onCall(gotCrops);
        } catch (Exception e) {
            e.printStackTrace();
            this.listener.onError(e);
        }
    }

    private List<LocalCrop> retrieveLocalCrops(List<String> ids){
        return new ArrayList<LocalCrop>();
    }
}