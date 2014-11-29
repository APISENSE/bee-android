//package com.apisense.bee.backend.user;
//
//import com.apisense.bee.AsyncTaskWithCallbacksTestSuite;
//import com.apisense.bee.BeeApplication;
//import com.apisense.bee.BeeRobolectricTestRunner;
//import fr.inria.bsense.service.BeeSenseServerServiceMock;
//import fr.inria.bsense.service.BeeSenseServiceManagerMock;
//import junit.framework.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//@RunWith(BeeRobolectricTestRunner.class)
//public class RegisterTaskTest extends AsyncTaskWithCallbacksTestSuite {
//    private RegisterTask task;
//    private static String username = "unameRegister";
//    private static String password = "azertyuiop";
//    private static String url = "hiveURL";
//
//    @Before
//    public void setUp() throws Exception {
//        super.setUp();
//        task =  new RegisterTask(new BeeSenseServiceManagerMock(), this);
//        BeeSenseServerServiceMock.accountCreated = false;
//        BeeSenseServerServiceMock.userConnected = false;
//        BeeSenseServerServiceMock.mockClearAccounts();
//    }
//
//    @Test
//    public final void testNotEnoughParams() throws InterruptedException {
//        task.execute();
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_ERROR, result);
//
//        Assert.assertFalse(BeeSenseServerServiceMock.accountCreated);
//        Assert.assertFalse(BeeSenseServerServiceMock.userConnected);
//    }
//
//    @Test
//    public final void testParamsEmpty() throws InterruptedException {
//        task.execute("", "", "");
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_ERROR, result);
//
//        Assert.assertFalse(BeeSenseServerServiceMock.accountCreated);
//        Assert.assertFalse(BeeSenseServerServiceMock.userConnected);
//    }
//
//    @Test
//    public final void testNoHiveParam() throws InterruptedException {
//        task.execute(username, password);
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertTrue(BeeSenseServerServiceMock.accountCreated);
//        Assert.assertTrue(BeeSenseServerServiceMock.userConnected);
//    }
//
//    @Test
//    public final void isAccountAlreadyCreated() throws InterruptedException {
//        task.execute(username, password, url);
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertTrue(BeeSenseServerServiceMock.accountCreated);
//        Assert.assertTrue(BeeSenseServerServiceMock.userConnected);
//
//        BeeSenseServerServiceMock.accountCreated = false;
//        // Trying to Register logged in user
//        signal = new CountDownLatch(1);
//        task.execute(username, password, url);
//        signal.await(15, TimeUnit.SECONDS);
//        Assert.assertEquals(BeeApplication.ASYNC_ERROR, result);
//
//        Assert.assertFalse(BeeSenseServerServiceMock.accountCreated);
//        Assert.assertTrue(BeeSenseServerServiceMock.userConnected);
//
//        // Faking a disconnection
//        BeeSenseServerServiceMock.userConnected = false;
//        // Trying to Register same user without being logged in
//        signal = new CountDownLatch(1);
//        task.execute(username, password, url);
//        signal.await(15, TimeUnit.SECONDS);
//        Assert.assertEquals(BeeApplication.ASYNC_ERROR, result);
//
//        Assert.assertFalse(BeeSenseServerServiceMock.accountCreated);
//        Assert.assertFalse(BeeSenseServerServiceMock.userConnected);
//    }
//}
