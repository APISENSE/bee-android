package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.Callbacks.OnCropStarted;
import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.games.SimpleGameAchievement;
import com.apisense.bee.ui.fragment.CategoryStoreFragment;
import com.apisense.bee.ui.fragment.HomeStoreFragment;
import com.apisense.bee.ui.fragment.NotFoundFragment;
import com.apisense.bee.utils.CropPermissionHandler;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.store.Crop;
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
    private APISENSE.Sdk apisenseSdk;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private CropPermissionHandler lastCropPermissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
        apisenseSdk = ((BeeApplication) getApplication()).getSdk();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_read_qrcode:
                installFromQRCode();
                break;
        }
        return false;
    }

    private void installFromQRCode() {
        Intent qrActivity = new Intent(this, QRScannerActivity.class);
        startActivityForResult(qrActivity, QRScannerActivity.INSTALL_FROM_QR);
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        // React only if the user actually scanned a QRcode
        if (request == QRScannerActivity.INSTALL_FROM_QR && response == RESULT_OK) {
            String cropID = data.getStringExtra(QRScannerActivity.CROP_ID_KEYWORD);
            apisenseSdk.getCropManager().installSpecific(cropID, new APSCallback<Crop>() {
                @Override
                public void onDone(Crop crop) {
                    lastCropPermissionHandler = new CropPermissionHandler(StoreActivity.this, crop,
                            new OnCropStarted(StoreActivity.this) {
                                @Override
                                public void onDone(Crop crop) {
                                    super.onDone(crop);
                                    // Installation complete, return to home activity
                                    finish();
                                }
                            });
                    lastCropPermissionHandler.startOrRequestPermissions();
                }

                @Override
                public void onError(Exception e) {
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            e.getMessage(),
                            Snackbar.LENGTH_LONG
                    ).show();
                }
            });
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (lastCropPermissionHandler != null) {
            lastCropPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
