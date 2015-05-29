package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.apisense.bee.R;
import com.apisense.bee.ui.adapter.PrivacyGridAdapter;
import com.apisense.bee.ui.entity.PrivacyGridItem;

import java.util.ArrayList;
import java.util.List;

import fr.inria.asl.facade.IFacade;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.service.BeeSenseServiceManager;


public class PrivacyActivity extends ActionBarActivity {

    private final String TAG = this.getClass().getSimpleName();
    private final List<PrivacyGridItem> sensor = new ArrayList<PrivacyGridItem>();
    private GridView sensorGridView;
    private PrivacyGridAdapter sensorGridAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        for (Class<? extends IFacade> facade : APISENSE.apisense().getPrivacyFacade()) {
            final String[] description = BeeSenseServiceManager.getPrivcayFacadeInfo(facade);
            if (description[2] == null)
                sensor.add(new PrivacyGridItem(description[0], R.drawable.ic_sensor_on, R.drawable.ic_sensor_off, true));
            else
                sensor.add(new PrivacyGridItem(description[0], Integer.valueOf(description[2]), Integer.valueOf(description[3]), true));
        }

        sensorGridAdapter = new PrivacyGridAdapter(this, sensor);
        sensorGridView = (GridView) findViewById(R.id.privacy_sensor_grid);
        sensorGridView.setAdapter(sensorGridAdapter);
        sensorGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                sensor.get(position).isActivated = !sensor.get(position).isActivated;
                sensorGridAdapter.notifyDataSetChanged();
            }
        });
    }
}
