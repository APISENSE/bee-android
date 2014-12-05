package com.apisense.bee.backend.experiment;

import android.content.Context;
import com.apisense.core.api.Log;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.core.api.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * Task to fetch every experiment installed (i.e. subscribed) by the user.
 *
 */
public class RetrieveInstalledExperimentsTask {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private Callback<List<APSLocalCrop>> listener;

    public RetrieveInstalledExperimentsTask(Context context, Callback<List<APSLocalCrop>> listener) {
        this.context = context;
        this.listener = listener;
    }

    public void execute() {
        try {
            List<String> returnIds = APS.getInstalledCrop(context);
            for (String exp: returnIds) {
                Log.i(TAG, exp);
            }
            List<APSLocalCrop> gotCrops = retrieveLocalCrops(returnIds);
            this.listener.onCall(gotCrops);
        } catch (Exception e) {
            e.printStackTrace();
            this.listener.onError(e);
        }
    }

    private List<APSLocalCrop> retrieveLocalCrops(List<String> ids){
        List<APSLocalCrop> result = new ArrayList<>();
        for (String id: ids){
            try {
                result.add(APS.getCropDescription(context, id));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}