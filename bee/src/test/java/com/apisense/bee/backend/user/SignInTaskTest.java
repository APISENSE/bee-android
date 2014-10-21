package com.apisense.bee.backend.user;

import com.apisense.bee.AsyncTaskWithCallbacksTestSuite;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.BeeRobolectricTestRunner;
import fr.inria.bsense.service.BeeSenseServerServiceMock;
import fr.inria.bsense.service.BeeSenseServiceManagerMock;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(BeeRobolectricTestRunner.class)
public class SignInTaskTest extends AsyncTaskWithCallbacksTestSuite {
    private SignInTask task;
    private static String username = "unameConnection";
    private static String password = "azertyuiop";
    private static String url = "hiveURL";


    @Before
    public void setUp() throws Exception {
        super.setUp();
        task =  new SignInTask(new BeeSenseServiceManagerMock(), this);
        BeeSenseServerServiceMock.userConnected = false;
        BeeSenseServerServiceMock.accountUpdated = false;
    }

    @Test
    public final void testNotEnoughParams() throws InterruptedException {
        task.execute();
        signal.await(15, TimeUnit.SECONDS);

        Assert.assertNotSame(result, Integer.MIN_VALUE);
        Assert.assertEquals(BeeApplication.ASYNC_ERROR, result);

        Assert.assertFalse(BeeSenseServerServiceMock.userConnected);
    }

    @Test
    public final void testParamsEmpty() throws InterruptedException {
        task.execute("", "", "");
        signal.await(15, TimeUnit.SECONDS);

        Assert.assertNotSame(result, Integer.MIN_VALUE);
        Assert.assertEquals(BeeApplication.ASYNC_ERROR, result);

        Assert.assertFalse(BeeSenseServerServiceMock.userConnected);
        Assert.assertFalse(BeeSenseServerServiceMock.accountUpdated);
    }

    @Test
    public final void testNoHiveParam() throws InterruptedException {
        task.execute(username, password);
        signal.await(15, TimeUnit.SECONDS);

        Assert.assertNotSame(result, Integer.MIN_VALUE);
        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);

        Assert.assertTrue(BeeSenseServerServiceMock.userConnected);
        Assert.assertTrue(BeeSenseServerServiceMock.accountUpdated);

    }

    @Test
    public final void testConnection() throws InterruptedException {
        task.execute(username, password);
        signal.await(15, TimeUnit.SECONDS);

        Assert.assertNotSame(result, Integer.MIN_VALUE);
        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);

        Assert.assertTrue(BeeSenseServerServiceMock.userConnected);
        Assert.assertTrue(BeeSenseServerServiceMock.accountUpdated);

    }

}
