//package com.apisense.bee.backend.experiment;
//
//import com.apisense.bee.AsyncTaskWithCallbacksTestSuite;
//import com.apisense.bee.BeeApplication;
//import com.apisense.bee.BeeRobolectricTestRunner;
//import fr.inria.bsense.service.BeeSenseServerServiceMock;
//import fr.inria.bsense.service.BeeSenseServiceManagerMock;
//import junit.framework.Assert;
//import org.json.JSONException;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@RunWith(BeeRobolectricTestRunner.class)
//public class RetrieveAvailableExperimentsTaskTest extends AsyncTaskWithCallbacksTestSuite{
//
//    private RetrieveAvailableExperimentsTask task;
//
//    public static final String FILTER_NO_RESULT = "unknownFilter";
//    public static final String FILTER_RESULT = "knownFilter";
//
//    @Before
//    public void setUp() throws Exception {
//        super.setUp();
//        task =  new RetrieveAvailableExperimentsTask(new BeeSenseServiceManagerMock(), this);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        BeeSenseServerServiceMock.availableExperiments = new ArrayList<Experiment>();
//    }
//
//    @Test
//    public final void testNoFilter() throws InterruptedException, JSONException {
//        Experiment exp1 = new Experiment(getExperimentJson());
//        Experiment exp2 = new Experiment(getExperimentJson());
//        List<Experiment> experimentsList = new ArrayList<Experiment>();
//        experimentsList.add(exp1);
//        experimentsList.add(exp2);
//        BeeSenseServerServiceMock.availableExperiments = experimentsList;
//        task.setIndex(0);
//
//        task.execute();
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertNotNull(response);
//        Assert.assertTrue(response instanceof List);
//        Assert.assertFalse(((List) response).isEmpty());
//        Assert.assertEquals(experimentsList, response);
//    }
//
//    @Test
//    public final void testEmptyFilter() throws InterruptedException, JSONException {
//        List<Experiment> experimentsList = new ArrayList<Experiment>();
//        experimentsList.add(new Experiment(getExperimentJson()));
//        experimentsList.add(new Experiment(getExperimentJson()));
//        BeeSenseServerServiceMock.availableExperiments = experimentsList;
//        task.setIndex(0);
//
//        task.execute("");
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertNotNull(response);
//        Assert.assertTrue(response instanceof List);
//        Assert.assertFalse(((List) response).isEmpty());
//        Assert.assertEquals(experimentsList, response);
//    }
//
//    @Test
//    public final void testExperimentsForFilter() throws InterruptedException, JSONException {
//        List<Experiment> experimentsList = new ArrayList<Experiment>();
//        experimentsList.add(new Experiment(getExperimentJson()));
//        experimentsList.add(new Experiment(getExperimentJson()));
//        BeeSenseServerServiceMock.availableExperiments = experimentsList;
//
//        task.setIndex(0);
//        task.execute(FILTER_RESULT);
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertNotNull(response);
//        Assert.assertTrue(response instanceof List);
//        Assert.assertFalse(((List) response).isEmpty());
//
//        Assert.assertEquals(experimentsList, response);
//    }
//
//    @Test
//    public final void testNoExperimentForFilter() throws InterruptedException {
//        task.setIndex(0);
//        task.execute(FILTER_NO_RESULT);
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertNotNull(response);
//        Assert.assertTrue(response instanceof List);
//        Assert.assertTrue(((List) response).isEmpty());
//    }
//
//    @Test
//    public final void testSearchRemoteException() throws InterruptedException {
//        // Assuming for test that a negative index will throw an exception
//        // (Mock compliant with this assumption)
//        task.setIndex(-1);
//
//        task.execute();
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_ERROR, result);
//
//        Assert.assertNotNull(response);
//        Assert.assertTrue(response instanceof List);
//        Assert.assertTrue(((List) response).isEmpty());
//    }
//
//    @Test
//    public final void testNullReturned() throws InterruptedException {
//        BeeSenseServerServiceMock.availableExperiments = null;
//
//        task.execute();
//        signal.await(15, TimeUnit.SECONDS);
//
//        Assert.assertNotSame(result, Integer.MIN_VALUE);
//        Assert.assertEquals(BeeApplication.ASYNC_SUCCESS, result);
//
//        Assert.assertNotNull(response);
//        Assert.assertTrue(response instanceof List);
//        Assert.assertTrue(((List) response).isEmpty());
//    }
//
//}
