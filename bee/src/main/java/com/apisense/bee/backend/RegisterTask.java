package com.apisense.bee.backend;

import android.os.AsyncTask;
import android.util.Log;
import fr.inria.bsense.APISENSE;

public class RegisterTask extends AsyncTask<String, Void, String> {
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTasksCallbacks listener;
    private final String DEFAULT_URL = "http://beta.apisense.io/hive";
    public RegisterTask(AsyncTasksCallbacks listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        // params[0] == pseudo
        // params[1] == password
        // params[2] == URL hive (optionnal)
        String pseudo = "";
        String password = "";
        String apisenseUrl = (params.length >= 3) ? params[2] : this.DEFAULT_URL;

        if (params[0].isEmpty() || params[1].isEmpty()) {
            Log.e(TAG, "Login or password is empty");
            return "error";
        } else {
            pseudo = params[0];
            password = params[1];
        }

        try {
            APISENSE.apisServerService().createAccount(apisenseUrl, "", pseudo, password, "");
            APISENSE.apisServerService().connect(pseudo, password);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        if (!APISENSE.apisServerService().isConnected())
            return "error";
        else
            return "success";
    }

    @Override
    protected void onPostExecute(final String response) {
        this.listener.onTaskCompleted(response);
    }

    @Override
    protected void onCancelled() {
        this.listener.onTaskCanceled();
    }
}