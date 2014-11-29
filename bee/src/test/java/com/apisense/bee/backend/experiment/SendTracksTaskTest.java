//package com.apisense.bee.backend.experiment;
//
//import com.apisense.bee.AsyncTaskWithCallbacksTestSuite;
//import com.apisense.bee.BeeApplication;
//import com.apisense.bee.BeeRobolectricTestRunner;
//import fr.inria.bsense.service.BeeSenseServiceManagerMock;
//import junit.framework.Assert;
//import org.json.JSONException;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.concurrent.TimeUnit;
//
//@RunWith(BeeRobolectricTestRunner.class)
//public class SendTracksTaskTest extends AsyncTaskWithCallbacksTestSuite {
//    private SendTracksTask task;
//
//    @Before
//    public void setUp() throws Exception {
//        super.setUp();
//        task =  new SendTracksTask(new BeeSenseServiceManagerMock(), this);
//    }
//
//    @Test
//    public final void testNoExperimentGiven() throws InterruptedException, JSONException {
//        task.execute();
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_ERROR, result);
//
//        Assert.assertNull(response);
//    }
//
//    @Test
//    public final void testExperimentGiven() throws InterruptedException, JSONException {
//        Experiment exp = new Experiment(getExperimentJson());
//
//        task.execute(exp);
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertNull(response);
//    }
//}
