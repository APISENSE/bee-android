package com.apisense.bee.backend.experiment;

import com.apisense.bee.AsyncTaskWithCallbacksTestSuite;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.BeeRobolectricTestRunner;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BeeSenseServiceManagerMock;
import junit.framework.Assert;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(BeeRobolectricTestRunner.class)
public class StartStopExperimentTaskTest extends AsyncTaskWithCallbacksTestSuite{
    private StartStopExperimentTask task;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        task =  new StartStopExperimentTask(new BeeSenseServiceManagerMock(), this);
    }

    @Test
    public final void testExperimentAlreadyStarted() throws JSONException, InterruptedException {
        // Test definition
        Experiment exp = new Experiment(getExperimentJson());
        exp.state = true;

        // Execute & wait for thread)
        task.execute(exp);
        signal.await(10, TimeUnit.SECONDS);

        // Assertions
        Assert.assertNotSame(result, Integer.MIN_VALUE);
        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);

        Assert.assertNotNull(response);
        Assert.assertEquals(StartStopExperimentTask.EXPERIMENT_STOPPED, response);
        Assert.assertFalse(exp.state);
    }

    @Test
    public final void testExperimentAlreadyStopped() throws JSONException, InterruptedException {
        // Test definition
        Experiment exp = new Experiment(getExperimentJson());
        exp.state = false;

        // Execute & wait for thread)
        task.execute(exp);
        signal.await(10, TimeUnit.SECONDS);

        // Assertions
        Assert.assertNotSame(result, Integer.MIN_VALUE);
        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);

        Assert.assertNotNull(response);
        Assert.assertEquals(StartStopExperimentTask.EXPERIMENT_STARTED, response);
        Assert.assertTrue(exp.state);
    }
}
