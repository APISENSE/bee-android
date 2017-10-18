package com.apisense.bee.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.apisense.bee.ui.adapter.CropField;
import com.apisense.bee.ui.adapter.DividerItemDecoration;
import com.apisense.bee.ui.adapter.SortComparator;
import com.apisense.bee.utils.CropPermissionHandler;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.store.Crop;

import static android.app.Activity.RESULT_OK;


public class StoreFragment extends BaseFragment {
    private final String TAG = getClass().getSimpleName();

    private APISENSE.Sdk apisenseSdk;
    private CropPermissionHandler lastCropPermissionHandler;
    private Unbinder unbinder;
    private static final int REQUEST_PERMISSION_QR_CODE = 1;

    private AvailableExperimentsRecyclerAdapter mAdapter;

    @BindView(R.id.action_read_qrcode)
    FloatingActionButton QRCodeButton;
    @BindView(R.id.store_experiments_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.store_empty_list)
    TextView mEmptyList;

    private boolean authorSortAscending = true;
    private boolean nameSortAscending = true;
    private Menu sortMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.fragment_store, container, false);
        unbinder = ButterKnife.bind(this, root);

        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();

        homeActivity.getSupportActionBar().setTitle(R.string.title_activity_store);
        homeActivity.selectDrawerItem(HomeActivity.DRAWER_STORE_IDENTIFIER);

        mAdapter = new AvailableExperimentsRecyclerAdapter(new AvailableExperimentsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Crop crop) {
                Bundle extra = new Bundle();
                extra.putParcelable("crop", crop);

                StoreDetailsFragment storeDetailsFragment = new StoreDetailsFragment();
                storeDetailsFragment.setArguments(extra);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.exp_container, storeDetailsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setHasFixedSize(true); // Performances
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        getExperiments();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.store_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_store_action_search);
        sortMenu = menu.findItem(R.id.menu_store_action_sort).getSubMenu();
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v(TAG, "Looking for crops using substring: " + query);
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.v(TAG, "Looking for crops using substring: " + query);
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_store_action_sort_name:
                sort(CropField.NAME, nameSortAscending, item);
                nameSortAscending = !nameSortAscending;
                break;
            case R.id.menu_store_action_sort_author:
                sort(CropField.AUTHOR, authorSortAscending, item);
                authorSortAscending = !authorSortAscending;
                break;
            default:
                Log.w(TAG, "Unable to retrieve id in store menu (" + item.getItemId() + ")");
        }
        return super.onOptionsItemSelected(item);
    }

    private void sort(SortComparator<Crop> sortComparator, boolean ascending, MenuItem item) {
        if (ascending) {
            mAdapter.getComparator().sort(sortComparator);
            item.setIcon(R.drawable.ic_sort_az_white);
        } else {
            mAdapter.getComparator().reverseSort(sortComparator);
            item.setIcon(R.drawable.ic_sort_za_white);
        }

        // Set other menus to diamond arrow

        for (int i = 0; i < sortMenu.size(); i++) {
            if (!item.equals(sortMenu.getItem(i))) {
                sortMenu.getItem(i).setIcon(R.drawable.ic_sort_unsorted_white);
            }
        }
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
        mAdapter.setAvailableCrops(experiments);
        mAdapter.notifyDataSetChanged();
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
