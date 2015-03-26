package com.apisense.bee.ui.activity;

import android.test.ActivityInstrumentationTestCase2;

import com.apisense.bee.ui.fragment.AccountSettingsFragment;
import com.robotium.solo.Solo;

public class AccountSettingsFragmentTest extends ActivityInstrumentationTestCase2<AccountSettingsFragment> {

    private Solo activity;

    public AccountSettingsFragmentTest() {
        super(AccountSettingsFragment.class);
    }

    public void setUp() {
        activity = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() {
        activity.finishOpenedActivities();
    }

    // - - - TESTS HERE
}