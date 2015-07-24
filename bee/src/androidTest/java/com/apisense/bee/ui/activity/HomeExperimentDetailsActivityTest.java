package com.apisense.bee.ui.activity;

import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;

public class HomeExperimentDetailsActivityTest extends ActivityInstrumentationTestCase2<HomeExperimentDetailsActivity> {

    private Solo activity;

    public HomeExperimentDetailsActivityTest() {
        super(HomeExperimentDetailsActivity.class);
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