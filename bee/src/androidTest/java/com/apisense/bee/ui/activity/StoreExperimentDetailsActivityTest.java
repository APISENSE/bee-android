package com.apisense.bee.ui.activity;

import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;
import junit.framework.TestCase;

public class StoreExperimentDetailsActivityTest extends ActivityInstrumentationTestCase2<StoreExperimentDetailsActivity> {

    private Solo activity;

    public StoreExperimentDetailsActivityTest() {
        super(StoreExperimentDetailsActivity.class);
    }

    public void setUp() {
        activity = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown()  {
        activity.finishOpenedActivities();
    }

    // - - - TESTS HERE
}