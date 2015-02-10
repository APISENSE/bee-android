package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import com.apisense.bee.R;
import com.apisense.bee.games.GPGGameManager;
import com.apisense.bee.ui.fragment.*;
import com.viewpagerindicator.CirclePageIndicator;

import java.lang.Override;

public class SlideshowActivity extends FragmentActivity implements View.OnClickListener {

    final public static String KEY_AUTHENTICATION_ACTION = "apisense.authentication.key";

    final public static String LOGIN_ANONYMOUS_ACTION = "apisense.anonymous.action";

    final public static String LOGIN_ACTION = "apisense.signin.action";

    final public static String LOGIN_PSEUDO= "apisense.signin.login";

    final public static String LOGIN_PWD = "apisense.signin.pwd";

    final public static String REGISTER_ACTION = "apisense.register.action";

    final public static String REGISTER_PSEUDO = "apisense.register.pseudo";

    final public static String REGISTER_PWD = "apisense.register.pwd";

    final public static String REGISTER_URL = "apisense.register.url";

    /**
     * The number of pages (wizard steps) to show
     * Be careful if you are adding some slides, button listeners may not match
     */
    private static final int NUM_PAGES = 5;

    /* Page order */
    public final static int SIGNIN = 0;
    public final static int WHAT = 1;
    public final static int HOW = 2;
    public final static int REWARD = 3;
    public final static int REGISTER = 4;

    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        //Bind the title indicator to the adapter
        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Check if we are coming from Anonymous HomeActivity
        try {
            Intent intent = getIntent(); // gets the previously created intent
            Integer destination = intent.getIntExtra("goTo", WHAT);

            mPager.setCurrentItem(destination); // Coming from an other activity


        } catch (NullPointerException e) {
            mPager.setCurrentItem(WHAT); // Launching the app
        }

        // Add onClick listeners
        Button signInBtn = (Button) findViewById(R.id.signIn);
        Button registerBtn = (Button) findViewById(R.id.register);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(SIGNIN);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(REGISTER);
            }
        });
    }

    @Override
    protected void onStart() {
        // connect the GPG Game Manager
        GPGGameManager.getInstance().initialize(this);
        
        // Connect
        if(!GPGGameManager.getInstance().isResolvingError()) {
            GPGGameManager.getInstance().signin();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        GPGGameManager.getInstance().signout();
        super.onStop();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GPGGameManager.REQUEST_RESOLVE_ERROR) {
            GPGGameManager.getInstance().setResolvingStatus(false);
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!GPGGameManager.getInstance().isConnecting() && 
                        !GPGGameManager.getInstance().isConnected()) {
                    GPGGameManager.getInstance().signin();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            // connect the asynchronous sign in flow
            GPGGameManager.getInstance().signin();
        }
        else if (view.getId() == R.id.sign_out_button) {

            GPGGameManager.getInstance().signout();
            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Slide show adapter used to generate all slides
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case WHAT: return new WhatFragment();
                case HOW: return new HowFragment();
                case REWARD: return new RewardFragment();
                case SIGNIN: return new SignInFragment();
                case REGISTER: return new RegisterFragment();
                default: return new NotFoundFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
