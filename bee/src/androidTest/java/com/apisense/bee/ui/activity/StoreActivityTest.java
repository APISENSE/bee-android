package com.apisense.bee.ui.activity;

import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;
import junit.framework.TestCase;

public class StoreActivityTest extends ActivityInstrumentationTestCase2<StoreActivity> {

    private Solo activity;

    public StoreActivityTest() {
        super(StoreActivity.class);
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