package com.apisense.bee.ui.activity;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;

/* Just an easy test class  */
public class SlideshowActivityTest extends ActivityInstrumentationTestCase2<SlideshowActivity> {

    private boolean iAmTrue = false;

    public SlideshowActivityTest() {
        super(SlideshowActivity.class);
    }

    public void setUp() throws Exception {
        iAmTrue = true;
    }

    @Override
    public void tearDown() throws Exception {

    }

    // - - - TESTS HERE

    public void testOnCreate() throws Exception {
        Assert.assertTrue(iAmTrue);
    }

}