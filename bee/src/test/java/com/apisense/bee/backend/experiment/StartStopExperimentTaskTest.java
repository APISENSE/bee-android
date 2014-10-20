package com.apisense.bee.backend.experiment;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.BeeRobolectricTestRunner;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BeeSenseServiceManagerMock;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(BeeRobolectricTestRunner.class)
public class StartStopExperimentTaskTest implements AsyncTasksCallbacks{
    private CountDownLatch signal;
    private int result;
    private Object response;
    private StartStopExperimentTask task;

    private final static String EXP_DEFINITION =
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
        task =  new StartStopExperimentTask(new BeeSenseServiceManagerMock(), this);
    }

    @Test
    public final void testExperimentAlreadyStarted() throws JSONException, InterruptedException {
        Experiment exp = new Experiment(new JSONObject(EXP_DEFINITION));
        exp.state = true;
        task.execute(exp);
        // create a signal to let us know when our task is done.
        signal.await(15, TimeUnit.SECONDS);

        Assert.assertNotSame(result, Integer.MIN_VALUE);
        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);

        Assert.assertNotNull(response);
        Assert.assertEquals(StartStopExperimentTask.EXPERIMENT_STOPPED, response);
        Assert.assertFalse(exp.state);
    }

    @Test
    public final void testExperimentAlreadyStopped() throws JSONException, InterruptedException {
        Experiment exp = new Experiment(new JSONObject(EXP_DEFINITION));
        exp.state = false;
        task.execute(exp);
        // create a signal to let us know when our task is done.
        signal.await(10, TimeUnit.SECONDS);
        Assert.assertNotSame(result, Integer.MIN_VALUE);
        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);

        Assert.assertNotNull(response);
        Assert.assertEquals(StartStopExperimentTask.EXPERIMENT_STARTED, response);
        Assert.assertTrue(exp.state);
    }

    @Override
    public void onTaskCompleted(int result, Object response) {
        this.result = result;
        this.response = response;
        signal.countDown();
    }

    @Override
    public void onTaskCanceled() {

    }
}
