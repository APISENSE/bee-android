package com.apisense.bee.ui.activity;

import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;
import junit.framework.TestCase;

public class ExperimentDetailsActivityTest extends ActivityInstrumentationTestCase2<ExperimentDetailsActivity> {

    private Solo activity;

    public ExperimentDetailsActivityTest() {
        super(ExperimentDetailsActivity.class);
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