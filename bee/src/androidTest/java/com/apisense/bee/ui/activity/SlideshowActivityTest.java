package com.apisense.bee.ui.activity;

import android.view.View;
import com.apisense.bee.R;
import com.robotium.solo.Solo;
import android.test.ActivityInstrumentationTestCase2;
import junit.framework.Assert;

import java.io.IOException;

public class SlideshowActivityTest extends ActivityInstrumentationTestCase2<SlideshowActivity> {

    private Solo activity;

    private View signInBtn;
    private View registerBtn;
    private View skipBtn;

    public SlideshowActivityTest() {
        super(SlideshowActivity.class);
    }

    public void setUp() {
        activity = new Solo(getInstrumentation(), getActivity());

        signInBtn = activity.getView(R.id.signIn);
        skipBtn = activity.getView(R.id.skip);
        registerBtn = activity.getView(R.id.register);
    }

    @Override
    public void tearDown() throws IOException {
        activity.finishOpenedActivities();
    }

    // Test buttons

    public void testSkipButton() {
        activity.clickOnView(skipBtn);
        activity.assertCurrentActivity("Expected HomeActivity to be launched", HomeActivity.class);
    }

    public void testSignInButton() {
        activity.clickOnView(signInBtn);
            Assert.assertTrue(activity.waitForView(R.id.signInView));
    }

    public void testRegisterButton() {
        activity.clickOnView(registerBtn);
        Assert.assertTrue(activity.waitForView(R.id.registerView));
    }
}