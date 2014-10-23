package com.apisense.bee.ui.activity;

import android.view.View;
import com.apisense.bee.R;
import com.robotium.solo.Solo;
import android.test.ActivityInstrumentationTestCase2;

public class HomeActivityTest extends ActivityInstrumentationTestCase2<HomeActivity> {

    private Solo activity;

    public HomeActivityTest() {
        super(HomeActivity.class);
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