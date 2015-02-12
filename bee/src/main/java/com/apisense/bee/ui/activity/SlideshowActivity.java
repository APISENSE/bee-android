package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.apisense.bee.R;
import com.apisense.bee.games.GPGGameManager;
import com.apisense.bee.games.GameActionListener;
import com.apisense.bee.games.action.GameAchievement;
import com.apisense.bee.games.action.GameAction;
import com.apisense.bee.games.action.SignInGameAchievement;
import com.apisense.bee.games.utils.BaseGameActivity;
import com.apisense.bee.games.utils.GameHelper;
import com.apisense.bee.ui.fragment.HowFragment;
import com.apisense.bee.ui.fragment.NotFoundFragment;
import com.apisense.bee.ui.fragment.RegisterFragment;
import com.apisense.bee.ui.fragment.RewardFragment;
import com.apisense.bee.ui.fragment.SignInFragment;
import com.apisense.bee.ui.fragment.WhatFragment;
import com.viewpagerindicator.CirclePageIndicator;

import fr.inria.asl.utils.Log;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.APISENSEListenner;
import fr.inria.bsense.service.BeeSenseServiceManager;

public class SlideshowActivity extends BaseGameActivity implements View.OnClickListener, GameActionListener, GameHelper.GameHelperListener {

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
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        GameHelper gh = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gh.enableDebugLog(true);
        gh.setConnectOnStart(true);
        this.mHelper = gh;
        mHelper.setup(this);

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
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Add onClick listeners
        //Button signInBtn = (Button) findViewById(R.id.sign_in_button);
        Button registerBtn = (Button) findViewById(R.id.register);
        // Button skipBtn = (Button) findViewById(R.id.skip);

       /* signInBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(SIGNIN);
            }
        });*/

        registerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(REGISTER);
            }
        });

        /* skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent slideIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(slideIntent);
                finish();
            }
        }); */

        // Init APISENSE and check if already connected, just go to home Activity
        APISENSE.init(getApplicationContext(), new APISENSEListenner() {
            @Override
            public void onConnected(BeeSenseServiceManager beeSenseServiceManager) {
                if (APISENSE.apisServerService().isConnected()) {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            boolean signed = GPGGameManager.getInstance().signin();

            Log.getInstance().i("Bee GPG isConnected/signed : " + GPGGameManager.getInstance().isConnected() + " " + signed);
            // connect the asynchronous sign in flow
            if (GPGGameManager.getInstance().isConnected()) {

                mPager.setCurrentItem(SIGNIN);

                handleGameAction(new SignInGameAchievement());
            }
            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.sign_out_button) {

            //GPGGameManager.getInstance().signout();
            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void handleGameAction(GameAction action) {
        GPGGameManager.getInstance().pushAchievement((GameAchievement) action);
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
                case WHAT:
                    return new WhatFragment();
                case HOW:
                    return new HowFragment();
                case REWARD:
                    return new RewardFragment();
                case SIGNIN:
                    return new SignInFragment();
                case REGISTER:
                    return new RegisterFragment();
                default:
                    return new NotFoundFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
