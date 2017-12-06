package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.callbacks.OnCropStarted;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.bee.ui.activity.QRScannerActivity;
import com.apisense.bee.ui.adapter.AvailableExperimentsRecyclerAdapter;
import com.apisense.bee.ui.adapter.DividerItemDecoration;
import com.apisense.bee.utils.CropPermissionHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.apisense.sdk.core.store.Crop;
import io.apisense.sdk.core.store.StoreOptions;

import static android.app.Activity.RESULT_OK;

public class StoreFragment extends SortedCropsFragment {
    private CropPermissionHandler lastCropPermissionHandler;
    private Unbinder unbinder;
    private static final int REQUEST_PERMISSION_QR_CODE = 1;

    @BindView(R.id.action_read_qrcode)
    FloatingActionButton QRCodeButton;
    @BindView(R.id.store_experiments_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.store_empty_list)
    TextView emptyListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_store, container, false);
        unbinder = ButterKnife.bind(this, root);

        homeActivity.getSupportActionBar().setTitle(R.string.title_activity_store);
        homeActivity.selectDrawerItem(HomeActivity.DRAWER_STORE_IDENTIFIER);

        experimentsAdapter = new AvailableExperimentsRecyclerAdapter(getActivity());
        mRecyclerView.setAdapter(experimentsAdapter);

        mRecyclerView.setHasFixedSize(true); // Performances
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
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

    private void getExperiments() {
        apisenseSdk.getStoreManager().findAllCrops(new StoreOptions(true),
                new OnExperimentsRetrieved(getActivity(), emptyListView));
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
            apisenseSdk.getCropManager().installOrUpdate(cropID, new BeeAPSCallback<Crop>(getActivity()) {
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
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
