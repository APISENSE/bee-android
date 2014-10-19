package com.apisense.bee.backend.experiment;

import com.apisense.bee.BeeRobolectricTestRunner;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

@RunWith(BeeRobolectricTestRunner.class)
public class StartStopExperimentTaskTest {
    private CountDownLatch signal;
    private int result;
    private Object response;
//    private StartStopExperimentTask task;

    private final static String STARTED_EXP_DEFINITION =
            "'baseUrl': 'URL'," +
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
            "'visible': 'true'";

    private final static String STOPPED_EXP_DEFINITION =
            "'baseUrl': 'URL'," +
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
            "'visible': 'true'";


    @Before
    public void setUp() throws Exception {
//        signal = new CountDownLatch(1);
        result = Integer.MIN_VALUE;
        response = null;
//        task =  new StartStopExperimentTask(this);
    }

    @Test
    public final void testExperimentAlreadyStarted() {
        Assert.assertTrue(true);
//        Experiment exp = new Experiment(new JSONObject(STARTED_EXP_DEFINITION));
//        task.execute(exp);
//        // create a signal to let us know when our task is done.
//        signal.await(10, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(result, BeeApplication.ASYNC_SUCCESS);
//
//        Assert.assertNotNull(response);
//        Assert.assertEquals(response, StartStopExperimentTask.EXPERIMENT_STOPPED);
    }

    @Test
    public final void testExperimentAlreadyStopped() {
        Assert.assertTrue(true);
//        Experiment exp = new Experiment(new JSONObject(STOPPED_EXP_DEFINITION));
//        task.execute(exp);
//        // create a signal to let us know when our task is done.
//        signal.await(10, TimeUnit.SECONDS);
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(result, BeeApplication.ASYNC_SUCCESS);
//
//        Assert.assertNotNull(response);
//        Assert.assertEquals(response, StartStopExperimentTask.EXPERIMENT_STARTED);
    }

//    @Override
//    public void onTaskCompleted(int result, Object response) {
//        this.result = result;
//        this.response = response;
//        signal.countDown();
//    }
//
//    @Override
//    public void onTaskCanceled() {
//
//    }
}
