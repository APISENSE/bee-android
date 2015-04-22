package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.ui.fragment.AboutSettingsFragment;
import com.apisense.bee.ui.fragment.AccountSettingsFragment;
import com.apisense.bee.ui.fragment.GeneralSettingsFragment;
import com.apisense.bee.ui.fragment.NotFoundFragment;
import com.astuetz.PagerSlidingTabStrip;

/**
 * Created by Warnant on 26-03-15.
 */
public class SettingsActivity extends BeeGameActivity {


    /**
     * The number of pages (wizard steps) to show
     * Be careful if you are adding some slides, button listeners may not match
     */
    private static final int NUM_PAGES = 3;

    /* Page order */
    private final static int ACCOUNT = 0;
    private final static int GENERAL = 1;
    private final static int ABOUT = 2;
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(ACCOUNT);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mPager);

    }

    /**
     * Slide show adapter used to generate all slides
     */
    private class SettingsPagerAdapter extends FragmentPagerAdapter {
        public SettingsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case ACCOUNT:
                    return new AccountSettingsFragment();
                case GENERAL:
                    return new GeneralSettingsFragment();
                case ABOUT:
                    return new AboutSettingsFragment();
                default:
                    return new NotFoundFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case ACCOUNT:
                    return getString(R.string.settings_section_account).toUpperCase();
                case GENERAL:
                    return getString(R.string.settings_section_general).toUpperCase();
                case ABOUT:
                    return getString(R.string.settings_section_about).toUpperCase();
                default:
                    return "";
            }

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
