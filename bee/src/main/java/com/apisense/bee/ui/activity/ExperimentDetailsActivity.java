package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.apisense.bee.R;
import com.apisense.bee.ui.entity.ExperimentSerializable;
import fr.inria.bsense.appmodel.Experiment;

import java.io.Serializable;
import java.util.Objects;

public class ExperimentDetailsActivity extends Activity {

    TextView mExperimentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_details);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        initializeViews();
        displayExperimentInformation();
    }

    public void initializeViews() {
        mExperimentName = (TextView) findViewById(R.id.exp_details_name);
    }
    public void displayExperimentInformation() {
        Bundle b = getIntent().getExtras();
        // TODO : Switch to parcelable when available
        // Experiment exp =  b.getParcelable("experiment");
        ExperimentSerializable exp = (ExperimentSerializable) b.getSerializable("experiment");
        mExperimentName.setText(exp.getName());
    }

    private boolean bundleContains(String name) {
        return getIntent().getExtras().getString(name).contains(null);
    }

    private Object getValue(String name) {
        return getIntent().getExtras().getString("name");
    }

    // - - - -

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.experiment_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
