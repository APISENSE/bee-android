package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.apisense.bee.R;
import com.apisense.bee.ui.adapter.PrivacyGridAdapter;
import com.apisense.bee.ui.entity.PrivacyGridItem;

import java.util.ArrayList;
import java.util.List;


public class PrivacyActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();

    private GridView sensorGridView;
    private PrivacyGridAdapter sensorGridAdapter;
    private final List<PrivacyGridItem> sensor = new ArrayList<PrivacyGridItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

//        for (Class<? extends IFacade> facade : APISENSE.apisense().getPrivacyFacade()){
//            final String[] description = BeeSenseServiceManager.getPrivcayFacadeInfo(facade);
//            if(description[2] == null)
//                sensor.add(new PrivacyGridItem(description[0], R.drawable.ic_sensor_on, R.drawable.ic_sensor_off, true));
//            else
//                sensor.add(new PrivacyGridItem(description[0], Integer.valueOf(description[2]), Integer.valueOf(description[3]), true));
//        }

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
