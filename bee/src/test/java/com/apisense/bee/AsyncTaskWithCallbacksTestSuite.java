package com.apisense.bee;

import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import org.junit.Before;

import java.util.concurrent.CountDownLatch;

/**
 * Define common usages to test AsyncTasks using Callbacks.
 */
public abstract class AsyncTaskWithCallbacksTestSuite implements AsyncTasksCallbacks {
    protected CountDownLatch signal;
    protected int result;
    protected Object response;
    protected boolean canceled;

    // TODO: Make a nicer experiment generator (to produce different experiments for tests)
    protected final static String EXP_DEFINITION =
            "{'baseUrl': 'URL'," +
                    "'collector': '/upload',"+
                    "'copyright': '',"+
                    "'description': '',"+
                    "'id': '9999',"+
                    "'language': 'Javascript',"+
                    "'mainScript': 'main.js',"+
                    "'name': 'StartedExp',"+
                    "'niceName': 'StartedExp',"+
                    "'orgDescription': '',"+
                    "'organization': 'unitTests',"+
                    "'remoteState': 'started',"+
                    "'type': 'android',"+
                    "'uuid': '7859bf08-1f9d-4597-8c24-dd1b9a992751',"+
                    "'version': '4.2',"+
                    "'visible': 'true'," +
                    "'state': 'true'}";

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