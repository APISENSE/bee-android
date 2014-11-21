package com.apisense.bee.ui.activity;

import android.app.Activity;
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
import com.apisense.bee.ui.fragment.*;
import com.viewpagerindicator.CirclePageIndicator;

public class SlideshowActivity extends FragmentActivity {

    /**
     * The number of pages (wizard steps) to show
     * Be careful if you are adding some slides, button listeners may not match
     */
    private static final int NUM_PAGES = 5;

    /* Page order */
    private final static int SIGNIN = 0;
    private final static int WHAT = 1;
    private final static int HOW = 2;
    private final static int REWARD = 3;
    private final static int REGISTER = 4;

    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // Check if we are coming from Anonymous HomeActivity
        try {
            Intent intent = getIntent(); // gets the previously created intent
            String destination = intent.getStringExtra("goTo");
            if (destination.equals("register")) {
                mPager.setCurrentItem(REGISTER); // Coming from an other activity
            } else {
                mPager.setCurrentItem(WHAT); // Default
            }
        } catch (NullPointerException e) {
            mPager.setCurrentItem(WHAT); // Launching the app
        }

        //Bind the title indicator to the adapter
        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Add onClick listeners
        Button signInBtn = (Button) findViewById(R.id.signIn);
        Button registerBtn = (Button) findViewById(R.id.register);
        Button skipBtn = (Button) findViewById(R.id.skip);

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

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = generateSkipIntent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private Intent generateSkipIntent() {
        Intent intent = new Intent();
        intent.putExtra("action", "anonymousLogin");
        return intent;
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
