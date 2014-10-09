package com.apisense.bee.backend;

import android.os.AsyncTask;
import android.util.Log;
import fr.inria.bsense.APISENSE;

public class SignUpTask extends AsyncTask<String, Void, String> {
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTasksCallbacks listener;

    public SignUpTask(AsyncTasksCallbacks listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        // params[0] == login
        // params[1] == password
        // params[2] == URL hive (optionnal)
        String email = params[0];
        String password = params[1];
        String apisenseUrl = (params.length >= 3) ? params[2] : "";

        if (params[0].isEmpty() || params[1].isEmpty()) {
            Log.e(TAG, "login or password is empty");
            return "error";
        }
        try {
            APISENSE.apisServerService().createAccount(apisenseUrl, "", email, password, "");
            APISENSE.apisServerService().connect(email, password);
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