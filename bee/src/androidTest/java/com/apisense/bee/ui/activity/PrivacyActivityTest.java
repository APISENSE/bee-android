package com.apisense.bee.ui.activity;

import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;
import junit.framework.TestCase;

public class PrivacyActivityTest extends ActivityInstrumentationTestCase2<PrivacyActivity> {

    private Solo activity;

    public PrivacyActivityTest() {
        super(PrivacyActivity.class);
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