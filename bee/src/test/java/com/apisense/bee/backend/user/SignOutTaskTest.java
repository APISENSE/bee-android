//package com.apisense.bee.backend.user;
//
//import com.apisense.bee.AsyncTaskWithCallbacksTestSuite;
//import com.apisense.bee.BeeApplication;
//import com.apisense.bee.BeeRobolectricTestRunner;
//import fr.inria.bsense.service.BeeSenseMobileServiceMock;
//import fr.inria.bsense.service.BeeSenseServerServiceMock;
//import fr.inria.bsense.service.BeeSenseServiceManagerMock;
//import junit.framework.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.concurrent.TimeUnit;
//
//@RunWith(BeeRobolectricTestRunner.class)
//public class SignOutTaskTest extends AsyncTaskWithCallbacksTestSuite {
//    private SignOutTask task;
//
//    @Before
//    public void setUp() throws Exception {
//        super.setUp();
//        task =  new SignOutTask(new BeeSenseServiceManagerMock(), this);
//        BeeSenseServerServiceMock.userConnected = true;
//        BeeSenseMobileServiceMock.experimentStopped = false;
//        BeeSenseMobileServiceMock.tracksSent = false;
//    }
//
//    @Test
//    public final void testDisconnection() throws InterruptedException {
//        task.execute();
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertFalse(BeeSenseServerServiceMock.userConnected);
//        Assert.assertTrue(BeeSenseMobileServiceMock.experimentStopped);
//        Assert.assertTrue(BeeSenseMobileServiceMock.tracksSent);
//    }
//
//}
