package com.apisense.bee.backend.experiment;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.apisense.bee.R;
import fr.inria.apislog.APISLog;
import fr.inria.asl.http.HttpRequestUtils;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * AsyncTask to get statistics about one or all experiments subscribed
 *
 * @author Christophe Ribeiro <christophe.ribeiro@inria.fr>
 *
 */
public class FetchStatsTask extends AsyncTask<Experiment, Void, Boolean> {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected Boolean doInBackground(Experiment... params) {
        Experiment exp = params[0];
        if (exp == null) {
            try {
                APISENSE.statistic().fetchUploadStatistic();
                Log.i(TAG, "Fetch all statistics for all experiments");
            } catch (HttpRequestUtils.HttpRequestException e) {
                Log.e(TAG, "Error fetch statistics for all experiment | ErrorHTTP=" + e.errorCode);
                APISLog.send(e, APISLog.ERROR);
                return false;
            } catch (Exception e) {
                Log.e(TAG, "Error fetch statistics for all experiment | Error=" + e.getMessage());
                APISLog.send(e, APISLog.ERROR);
                return false;
            }
        }
        else
            try {
                APISENSE.statistic().fetchUploadStatistic(exp);
                Log.i(TAG, "Fetch statistics for experiment " + exp.name);
            } catch (HttpRequestUtils.HttpRequestException e1) {
                Log.e(TAG, "Error fetch statistics for experiment " + exp.name + " | ErrorHTTP=" + e1.errorCode);
                APISLog.send(e1, APISLog.ERROR);
                return false;
            } catch (Exception e1) {
                Log.e(TAG, "Error fetch statistics for experiment " + exp.name + " | Error=" + e1.getMessage());
                APISLog.send(e1, APISLog.ERROR);
                return false;
            }
        return true;
    }

}