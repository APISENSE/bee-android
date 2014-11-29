package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.apisense.bee.R;
import com.apisense.bee.ui.adapter.PrivacyGridAdapter;
import com.apisense.bee.ui.entity.PrivacyGridItem;

import java.util.ArrayList;
import java.util.List;


public class PrivacyActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();

    private PrivacyGridAdapter sensorGridAdapter;
    private final List<PrivacyGridItem> sensor = new ArrayList<PrivacyGridItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        initSensorGrid();
    }

    private void initSensorGrid(){
        sensorGridAdapter = new PrivacyGridAdapter(this, sensor);
        GridView sensorGridView = (GridView) findViewById(R.id.privacy_sensor_grid);

        sensorGridView.setEmptyView(findViewById(R.id.sensor_grid_empty_view));
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
