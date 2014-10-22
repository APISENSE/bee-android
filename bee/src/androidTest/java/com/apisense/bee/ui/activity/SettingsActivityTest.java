package com.apisense.bee.ui.activity;

import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;
import junit.framework.TestCase;

public class SettingsActivityTest extends ActivityInstrumentationTestCase2<SettingsActivity> {

    private Solo activity;

    public SettingsActivityTest() {
        super(SettingsActivity.class);
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