package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.RetrieveAvailableExperimentsTask;
import com.apisense.bee.backend.experiment.SubscribeUnsubscribeExperimentTask;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.games.event.MissionSubscribeEvent;
import com.apisense.bee.ui.activity.StoreExperimentDetailsActivity;
import com.apisense.bee.ui.adapter.AvailableExperimentsListAdapter;
import com.apisense.bee.ui.entity.ExperimentSerializable;

import java.util.ArrayList;
import java.util.List;

import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Created by Warnant on 22/04/2015.
 */
public class HomeStoreFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    // Content Adapter
    protected AvailableExperimentsListAdapter experimentsAdapter;
    // Asynchronous Task
    private RetrieveAvailableExperimentsTask experimentsRetrieval;
    private SubscribeUnsubscribeExperimentTask experimentChangeSubscriptionStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_store_home, container, false);

        // Setting up available experiments list behavior
        experimentsAdapter = new AvailableExperimentsListAdapter(getActivity().getBaseContext(),
                R.layout.fragment_experiment_store_element,
                new ArrayList<Experiment>());
        ListView subscribedExperiments = (ListView) root.findViewById(R.id.store_experiment_lists);
        subscribedExperiments.setEmptyView(root.findViewById(R.id.store_empty_list));
        subscribedExperiments.setAdapter(experimentsAdapter);
        subscribedExperiments.setOnItemClickListener(new OpenExperimentDetailsListener());
        subscribedExperiments.setOnItemLongClickListener(new SubscriptionListener());

        getExperiments();

        return root;
    }


    /**
     * Change the adapter dataSet with a newly fetched List of Experiment
     *
     * @param experiments The new list of experiments to show
     */
    public void setExperiments(List<Experiment> experiments) {
        this.experimentsAdapter.setDataSet(experiments);
    }


    // Callbacks definitions

    public void getExperiments() {
        if (experimentsRetrieval != null) {
            experimentsRetrieval.cancel(true);
        }
        // Creating new request to retrieve Experiments
        if (experimentsRetrieval == null) {
            experimentsRetrieval = new RetrieveAvailableExperimentsTask(APISENSE.apisense(), new OnExperimentsRetrieved());
            experimentsRetrieval.execute();
        }
    }

    private class OnExperimentsRetrieved implements AsyncTasksCallbacks {
        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentsRetrieval = null;
            List<Experiment> exp = (List<Experiment>) response;
            Log.i(TAG, "Number of Active Experiments: " + exp.size());

            // Updating listview
            setExperiments(exp);
            experimentsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTaskCanceled() {
            experimentsRetrieval = null;
        }
    }

    // TODO: Export (Un)Subscription indocator management to Adapter (with a boolean field 'subscribed' in the Experiment)
    private class onExperimentSubscriptionChanged implements AsyncTasksCallbacks {
        private View statusView;
        private View concernedView;

        public onExperimentSubscriptionChanged(View v) {
            super();
            this.concernedView = v;
            this.statusView = concernedView.findViewById(R.id.item);

        }

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentChangeSubscriptionStatus = null;
            String experimentName = ((TextView) concernedView.findViewById(R.id.experimentelement_sampletitle))
                    .getText().toString();
            String toastMessage = "";
            if (result == BeeApplication.ASYNC_SUCCESS) {
                switch ((Integer) response) {
                    case SubscribeUnsubscribeExperimentTask.EXPERIMENT_SUBSCRIBED:
                        toastMessage = String.format(getString(R.string.experiment_subscribed), experimentName);
                        BeeGameManager.getInstance().fireGameEventPerformed(new MissionSubscribeEvent((BeeGameActivity) getActivity()));
                        break;
                    case SubscribeUnsubscribeExperimentTask.EXPERIMENT_UNSUBSCRIBED:
                        toastMessage = String.format(getString(R.string.experiment_unsubscribed), experimentName);
                        break;
                }
                // User feedback
                Toast.makeText(getActivity().getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentChangeSubscriptionStatus = null;
        }
    }


    // Listeners definitions

    private class OpenExperimentDetailsListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), StoreExperimentDetailsActivity.class);
            Experiment exp = (Experiment) parent.getAdapter().getItem(position);

            Bundle bundle = new Bundle();
            // TODO : Prefer parcelable in the future. Problem : CREATOR method doesn't exist (to check)
            // bundle.putParcelable("experiment", getItem(position));
            // TODO : Maybe something extending Experiment and using JSONObject to init but it seems to be empty
            bundle.putSerializable("experiment", new ExperimentSerializable(exp));
            intent.putExtras(bundle); //Put your id to your next Intent
            startActivity(intent);
        }
    }

    private class SubscriptionListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Experiment exp = (Experiment) parent.getAdapter().getItem(position);
            if (experimentChangeSubscriptionStatus == null) {
                experimentChangeSubscriptionStatus = new SubscribeUnsubscribeExperimentTask(APISENSE.apisense(), new onExperimentSubscriptionChanged(view));
                experimentChangeSubscriptionStatus.execute(exp);
            }
            return true;
        }
    }
}
