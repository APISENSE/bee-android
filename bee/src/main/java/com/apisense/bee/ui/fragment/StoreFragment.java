package com.apisense.bee.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.callbacks.OnCropStarted;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.bee.ui.activity.QRScannerActivity;
import com.apisense.bee.ui.adapter.AvailableExperimentsRecyclerAdapter;
import com.apisense.bee.ui.adapter.DividerItemDecoration;
import com.apisense.bee.utils.CropPermissionHandler;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.store.Crop;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


public class StoreFragment extends BaseFragment {
    private final String TAG = getClass().getSimpleName();

    private APISENSE.Sdk apisenseSdk;
    private CropPermissionHandler lastCropPermissionHandler;
    private Unbinder unbinder;
    private static final int REQUEST_PERMISSION_QR_CODE = 1;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @BindView(R.id.action_read_qrcode) FloatingActionButton QRCodeButton;
    @BindView(R.id.store_experiments_list) RecyclerView mRecyclerView;
    @BindView(R.id.store_empty_list) TextView mEmptyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_store, container, false);
        unbinder = ButterKnife.bind(this, root);

        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();

        homeActivity.getSupportActionBar().setTitle(R.string.title_activity_store);
        homeActivity.selectDrawerItem(HomeActivity.DRAWER_STORE_IDENTIFIER);

        mRecyclerView.setHasFixedSize(true); // Performances
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        getExperiments();

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.action_read_qrcode)
    void installCropFromQRCode(View view) {
        if (cameraPermissionGranted()) {
            installFromQRCode();
        } else {
            String[] permissions = {android.Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_PERMISSION_QR_CODE);
        }
    }

    /**
     * Change the adapter dataSet with a newly fetched List of Experiment
     *
     * @param experiments The new list of experiments to show
     */
    private void setExperiments(List<Crop> experiments) {
        mAdapter = new AvailableExperimentsRecyclerAdapter(experiments, new AvailableExperimentsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Crop crop) {
                Bundle extra = new Bundle();
                extra.putParcelable("crop", crop);

                StoreDetailsFragment storeDetailsFragment = new StoreDetailsFragment();
                storeDetailsFragment.setArguments(extra);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.exp_container, storeDetailsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getExperiments() {
        apisenseSdk.getStoreManager().findAllCrops(new OnExperimentsRetrieved(getActivity()));
    }

    // Callbacks definitions

    private class OnExperimentsRetrieved extends BeeAPSCallback<List<Crop>> {
        public OnExperimentsRetrieved(Activity activity) {
            super(activity);
        }

        @Override
        public void onDone(List<Crop> crops) {
            Log.i(TAG, "Number of Active Experiments: " + crops.size());
            if (crops.size() > 0) {
                mEmptyList.setVisibility(View.GONE);
            }
            setExperiments(crops);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (lastCropPermissionHandler != null) {
            lastCropPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean cameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void installFromQRCode() {
        Intent qrActivity = new Intent(getActivity(), QRScannerActivity.class);
        startActivityForResult(qrActivity, QRScannerActivity.INSTALL_FROM_QR);
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        // React only if the user actually scanned a QRcode
        if (request == QRScannerActivity.INSTALL_FROM_QR && response == RESULT_OK) {
            String cropID = data.getStringExtra(QRScannerActivity.CROP_ID_KEYWORD);
            apisenseSdk.getCropManager().installSpecific(cropID, new BeeAPSCallback<Crop>(getActivity()) {
                @Override
                public void onDone(Crop crop) {
                    lastCropPermissionHandler = new CropPermissionHandler(getActivity(), crop,
                            new OnCropStarted(getActivity()) {
                                @Override
                                public void onDone(Crop crop) {
                                    super.onDone(crop);
                                }
                            });
                    lastCropPermissionHandler.startOrRequestPermissions();
                }

                @Override
                public void onError(Exception e) {
                    super.onError(e);
                    Toast.makeText(
                            getActivity(),
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        }
    }
}
