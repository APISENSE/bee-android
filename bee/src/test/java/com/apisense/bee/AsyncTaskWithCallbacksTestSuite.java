package com.apisense.bee;

import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Define common usages to test AsyncTasks using Callbacks.
 */
public abstract class AsyncTaskWithCallbacksTestSuite implements AsyncTasksCallbacks {
    protected CountDownLatch signal;
    protected int result;
    protected Object response;
    protected boolean canceled;


    public static JSONObject getExperimentJson() {
        return getExperimentJson(0);
    }

    public static JSONObject getExperimentJson(int id) {
        return getExperimentJson(id, "started");
    }

    public static JSONObject getExperimentJson(int id, String remoteState) {
        return getExperimentJson("testExperiment", id, remoteState);
    }

    public static JSONObject getExperimentJson(String expName, int id, String remoteState) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("'baseUrl': 'URL',");
        sb.append("'collector': '/upload',");
        sb.append("'copyright': '',");
        sb.append("'description': '',");
        sb.append("'id': '").append(id).append("',");
        sb.append("'language': 'Javascript',");
        sb.append("'mainScript': 'main.js',");
        sb.append("'name': '").append(expName).append("',");
        sb.append("'niceName': '").append(expName).append("',");
        sb.append("'orgDescription': '',");
        sb.append("'organization': 'unitTests',");
        sb.append("'remoteState': '").append(remoteState).append("',");
        sb.append("'type': 'android',");
        sb.append("'uuid': '").append(UUID.randomUUID()).append("',");
        sb.append("'version': '4.2',");
        sb.append("'visible': 'true'");
        sb.append("}");

        JSONObject expJSon = null;
        try {
            expJSon = new JSONObject(sb.toString());
        } catch (JSONException e) {
            expJSon = new JSONObject();
        }
        return expJSon;
    }

    @Before
    public void setUp() throws Exception {
        signal = new CountDownLatch(1);
        result = Integer.MIN_VALUE;
        response = null;
        canceled = false;
    }

    @Override
    public void onTaskCompleted(int result, Object response) {
        this.result = result;
        this.response = response;
        signal.countDown();
    }

    @Override
    public void onTaskCanceled() {
        canceled = true;
    }

}