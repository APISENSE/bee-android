package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.games.SimpleGameAchievement;
import com.apisense.bee.ui.fragment.CategoryStoreFragment;
import com.apisense.bee.ui.fragment.HomeStoreFragment;
import com.apisense.bee.ui.fragment.NotFoundFragment;
import com.astuetz.PagerSlidingTabStrip;

public class StoreActivity extends BeeGameActivity {

    /**
     * The number of pages (wizard steps) to show
     * Be careful if you are adding some slides, button listeners may not match
     */
    private static final int NUM_PAGES = 2;
    /* Page order */
    private final static int CATEGORIES = 0;
    private final static int HOME = 1;
    protected Toolbar toolbar;
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new StorePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(HOME);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mPager);
    }

    @Override
    public void onSignInSucceeded() {
        super.onSignInSucceeded();
        // New achievement unlocked!
        new SimpleGameAchievement(getString(R.string.achievement_curious_bee)).unlock(this);
    }

    private class StorePagerAdapter extends FragmentPagerAdapter {
        public StorePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case CATEGORIES:
                    return new CategoryStoreFragment();
                case HOME:
                    return new HomeStoreFragment();
                default:
                    return new NotFoundFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case CATEGORIES:
                    return getString(R.string.store_section_categories).toUpperCase();
                case HOME:
                    return getString(R.string.store_section_home).toUpperCase();
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
