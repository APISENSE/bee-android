//package com.apisense.bee.backend.experiment;
//
//import com.apisense.bee.AsyncTaskWithCallbacksTestSuite;
//import com.apisense.bee.BeeApplication;
//import com.apisense.bee.BeeRobolectricTestRunner;
//import fr.inria.bsense.appmodel.Experiment;
//import fr.inria.bsense.service.BeeSenseMobileServiceMock;
//import fr.inria.bsense.service.BeeSenseServiceManagerMock;
//import junit.framework.Assert;
//import org.json.JSONException;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.concurrent.TimeUnit;
//
//@RunWith(BeeRobolectricTestRunner.class)
//public class SubscribeUnsubscribeExperimentTaskTest extends AsyncTaskWithCallbacksTestSuite {
//    private SubscribeUnsubscribeExperimentTask task;
//
//
//    @BeforeClass
//    public static void setUpClass() {
//        SubscribeUnsubscribeExperimentTask.sMobService = new BeeSenseServiceManagerMock().getBSenseMobileService();
//    }
//
//        @Before
//    public void setUp() throws Exception {
//        super.setUp();
////        SubscribeUnsubscribeExperimentTask.sMobService = new BeeSenseServiceManagerMock().getBSenseMobileService();
//        task =  new SubscribeUnsubscribeExperimentTask(new BeeSenseServiceManagerMock(), this);
//    }
//
//    @Test
//    public final void testStateDoesNotAffectSubscription() throws JSONException, InterruptedException {
//        String expName = "InstalledExp";
//        Experiment installedExp = new Experiment(getExperimentJson(expName));
//        BeeSenseMobileServiceMock.installedExperiments.put(expName, installedExp);
//
//        installedExp.state = null;
//        Assert.assertTrue(SubscribeUnsubscribeExperimentTask.isSubscribedExperiment(installedExp));
//        installedExp.state = true;
//        Assert.assertTrue(SubscribeUnsubscribeExperimentTask.isSubscribedExperiment(installedExp));
//        installedExp.state = false;
//        Assert.assertTrue(SubscribeUnsubscribeExperimentTask.isSubscribedExperiment(installedExp));
//    }
//
//    @Test
//    public final void testNotInstalledNotSubscribed() throws JSONException, InterruptedException {
//        String expName = "NotInstalledExp";
//        Experiment notInstalledExp = new Experiment(getExperimentJson(expName));
//        Assert.assertFalse(SubscribeUnsubscribeExperimentTask.isSubscribedExperiment(notInstalledExp));
//    }
//
//    @Test
//    public final void testExperimentAlreadySubscribedAndStarted() throws JSONException, InterruptedException {
//        String expName = "expName";
//        Experiment exp = new Experiment(getExperimentJson(expName));
//        exp.state = true;
//        BeeSenseMobileServiceMock.installedExperiments.put(expName, exp);
//
//        task.execute(exp);
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertNotNull(response);
//        Assert.assertTrue(response instanceof Integer);
//        Assert.assertEquals(SubscribeUnsubscribeExperimentTask.EXPERIMENT_UNSUBSCRIBED, response);
//    }
//
//    @Test
//    public final void testExperimentAlreadySubscribedAndNotStarted() throws JSONException, InterruptedException {
//        String expName = "expName";
//        Experiment exp = new Experiment(getExperimentJson(expName));
//        exp.state = false;
//        BeeSenseMobileServiceMock.installedExperiments.put(expName, exp);
//
//        task.execute(exp);
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertNotNull(response);
//        Assert.assertTrue(response instanceof Integer);
//        Assert.assertEquals(SubscribeUnsubscribeExperimentTask.EXPERIMENT_UNSUBSCRIBED, response);
//    }
//
//    @Test
//    public final void testExperimentNotSubscribedYet() throws JSONException, InterruptedException {
//        Experiment exp = new Experiment(getExperimentJson());
//
//        task.execute(exp);
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertNotNull(response);
//        Assert.assertTrue(response instanceof Integer);
//        Assert.assertEquals(SubscribeUnsubscribeExperimentTask.EXPERIMENT_SUBSCRIBED, response);
//    }
//
//}