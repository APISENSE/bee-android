package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.Callbacks.BeeAPSCallback;
import com.apisense.bee.Callbacks.OnCropStarted;
import com.apisense.bee.Callbacks.OnCropSubscribed;
import com.apisense.bee.Callbacks.OnCropUnsubscribed;
import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.games.IncrementalGameAchievement;
import com.apisense.bee.games.SimpleGameAchievement;
import com.apisense.bee.ui.adapter.AvailableExperimentsListAdapter;
import com.apisense.bee.utils.CropPermissionHandler;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.adapter.SimpleAPSCallback;
import com.apisense.sdk.core.store.Crop;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends BeeGameActivity {
    /**
     * The number of pages (wizard steps) to show
     * Be careful if you are adding some slides, button listeners may not match
     */
    private final String TAG = getClass().getSimpleName();

    private FloatingActionButton qrcodebutton;
    protected Toolbar toolbar;
    private APISENSE.Sdk apisenseSdk;    private static final int REQUEST_PERMISSION_QR_CODE = 1;
    // Content Adapter
    protected AvailableExperimentsListAdapter experimentsAdapter;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private CropPermissionHandler lastCropPermissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        apisenseSdk = ((BeeApplication) getApplication()).getSdk();

        // Setting up available experiments list behavior
        experimentsAdapter = new AvailableExperimentsListAdapter(getBaseContext(),
                R.layout.list_item_store_experiment,
                new ArrayList<Crop>());
        ListView subscribedExperiments = (ListView) findViewById(R.id.store_experiment_lists);
        subscribedExperiments.setEmptyView(findViewById(R.id.store_empty_list));
        subscribedExperiments.setAdapter(experimentsAdapter);
        subscribedExperiments.setOnItemClickListener(new OpenExperimentDetailsListener());
        subscribedExperiments.setOnItemLongClickListener(new SubscriptionListener());

        getExperiments();

        this.qrcodebutton = (FloatingActionButton) findViewById(R.id.QRButton);
        this.qrcodebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraPermissionGranted()) {
                    installFromQRCode();
                } else {
                    String[] permissions = {android.Manifest.permission.CAMERA};
                    ActivityCompat.requestPermissions(StoreActivity.this, permissions, REQUEST_PERMISSION_QR_CODE);
                }
            }
        });
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
                if (cameraPermissionGranted()) {
                    installFromQRCode();
                } else {
                    String[] permissions = {android.Manifest.permission.CAMERA};
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_QR_CODE);
                }
                break;
        }
        return false;
    }

    private void installFromQRCode() {
        Intent qrActivity = new Intent(this, QRScannerActivity.class);
        startActivityForResult(qrActivity, QRScannerActivity.INSTALL_FROM_QR);
    }

    private boolean cameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        // React only if the user actually scanned a QRcode
        if (request == QRScannerActivity.INSTALL_FROM_QR && response == RESULT_OK) {
            String cropID = data.getStringExtra(QRScannerActivity.CROP_ID_KEYWORD);
            apisenseSdk.getCropManager().installSpecific(cropID, new BeeAPSCallback<Crop>(this) {
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
                    super.onError(e);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_QR_CODE) {
            boolean auth = true;
            for (int grantResult : grantResults) {
                auth = auth && grantResult == PackageManager.PERMISSION_GRANTED;
            }
            if (auth) {
                installFromQRCode();
            }
        } else {
            if (lastCropPermissionHandler != null) {
                lastCropPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    /**
     * Change the adapter dataSet with a newly fetched List of Experiment
     *
     * @param experiments The new list of experiments to show
     */
    public void setExperiments(List<Crop> experiments) {
        this.experimentsAdapter.setDataSet(experiments);
    }

    public void getExperiments() {
        apisenseSdk.getStoreManager().findAllCrops(new OnExperimentsRetrieved());
    }

    // Listeners definitions

    private class OpenExperimentDetailsListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), StoreExperimentDetailsActivity.class);
            Crop crop = (Crop) parent.getAdapter().getItem(position);

            Bundle bundle = new Bundle();
            bundle.putParcelable("crop", crop);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private class SubscriptionListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Crop crop = (Crop) parent.getAdapter().getItem(position);
            if (apisenseSdk.getCropManager().isInstalled(crop)) {
                apisenseSdk.getCropManager().unsubscribe(crop, new StoreCropUnsubscribed(crop));
            } else {
                lastCropPermissionHandler = new CropPermissionHandler(StoreActivity.this, crop,
                        new OnCropStarted(view.getContext()) {
                            @Override
                            public void onDone(Crop crop) {
                                super.onDone(crop);
                                experimentsAdapter.notifyDataSetChanged();
                            }
                        });
                apisenseSdk.getCropManager().subscribe(crop, new StoreCropSubscribed(crop));
            }
            return true;
        }
    }

    // Callbacks definitions

    private class OnExperimentsRetrieved extends SimpleAPSCallback<List<Crop>> {
        @Override
        public void onDone(List<Crop> crops) {
            Log.i(TAG, "Number of Active Experiments: " + crops.size());

            // Updating listview
            setExperiments(crops);
            experimentsAdapter.notifyDataSetChanged();
        }
    }

    private class StoreCropUnsubscribed extends OnCropUnsubscribed {
        public StoreCropUnsubscribed(Crop crop) {
            super(StoreActivity.this, crop.getName());
        }

        @Override
        public void onDone(Crop crop) {
            super.onDone(crop);
            // Increment every subscription related achievements
            new IncrementalGameAchievement(getString(R.string.achievement_bronze_wings)).increment(StoreActivity.this);
            new IncrementalGameAchievement(getString(R.string.achievement_silver_wings)).increment(StoreActivity.this);
            new IncrementalGameAchievement(getString(R.string.achievement_gold_wings)).increment(StoreActivity.this);
            new IncrementalGameAchievement(getString(R.string.achievement_crystal_wings)).increment(StoreActivity.this);
        }
    }

    private class StoreCropSubscribed extends OnCropSubscribed {
        public StoreCropSubscribed(Crop crop) {
            super(StoreActivity.this, crop, lastCropPermissionHandler);
        }
    }

}
