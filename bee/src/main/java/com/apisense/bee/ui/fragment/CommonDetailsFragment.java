package com.apisense.bee.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.callbacks.OnCropStarted;
import com.apisense.bee.utils.CropPermissionHandler;
import com.apisense.bee.utils.SensorsDrawer;
import io.apisense.sdk.APISENSE;
import io.apisense.sting.lib.Sensor;
import io.apisense.sdk.core.store.Crop;

import java.util.Set;

import butterknife.BindView;
import butterknife.Unbinder;

public class CommonDetailsFragment extends BaseFragment {

    @BindView(R.id.crop_detail_title) TextView nameView;
    @BindView(R.id.crop_detail_owner_and_version) TextView organizationView;
    @BindView(R.id.crop_detail_description) TextView descriptionView;
    @BindView(R.id.crop_sensors_detail_container) LinearLayout stingGridView;

    protected Crop crop;
    protected APISENSE.Sdk apisenseSdk;
    protected Unbinder unbinder;
    protected CropPermissionHandler cropPermissionHandler;

    private Set<Sensor> mAvailableSensors;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_common_details, container, false);
        setHasOptionsMenu(true);

        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();
        mAvailableSensors = apisenseSdk.getPreferencesManager().retrieveAvailableSensors();

        retrieveBundle();
        cropPermissionHandler = prepareCropPermissionHandler();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayExperimentInformation();
    }

    @Override
    public void setHasOptionsMenu(boolean hasMenu) {
        super.setHasOptionsMenu(hasMenu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        cropPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Private methods

    protected CropPermissionHandler prepareCropPermissionHandler() {
        return new CropPermissionHandler(getActivity(), crop, new OnCropStarted(getActivity()));
    }

    private void retrieveBundle() {
        Bundle bundle = this.getArguments();
        crop = bundle.getParcelable("crop");
    }

    protected void displayExperimentInformation() {
        nameView.setText(getString(R.string.exp_details_name, crop.getName()));
        organizationView.setText(getString(R.string.exp_details_organization, crop.getOwner()) +
                " - " + getString(R.string.exp_details_version, crop.getVersion()));
        descriptionView.setText(crop.getShortDescription());

        new SensorsDrawer(mAvailableSensors).draw(getContext(), stingGridView, crop.getUsedStings());
    }

    protected void doUpdate() {
        apisenseSdk.getCropManager().update(crop.getLocation(), new BeeAPSCallback<Crop>(getActivity()) {
            @Override
            public void onDone(Crop crop) {
                CommonDetailsFragment.this.crop = crop;
                displayExperimentInformation();

                Toast.makeText(
                        getActivity(),
                        getString(R.string.experiment_updated, crop.getName()),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
