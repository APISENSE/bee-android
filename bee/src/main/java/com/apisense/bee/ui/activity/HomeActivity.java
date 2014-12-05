package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.apisense.core.api.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.android.feedz.CardCrop;
import com.apisense.bee.R;
import com.apisense.bee.backend.user.SignOutTask;
import com.apisense.core.api.APSLogEvent;
import com.apisense.core.api.Callable;
import com.apisense.core.api.Callback;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    // Asynchronous Tasks
    //private RetrieveInstalledExperimentsTask experimentsRetrieval;
    //private StartStopExperimentTask experimentStartStopTask;
    private SignOutTask signOut;

    private BroadcastReceiver eventReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homebis_layout);

    }

    @Override
    protected void onStart() {
        super.onStart();

        updateProfile();

        eventReceiver = APS.registerToAPSEvent(this, new Callable<Void, APSLogEvent>() {
            @Override
            public Void call(APSLogEvent apsLogEvent) throws Exception {
                Log.i(TAG, "Got event (" + apsLogEvent + ") for crop: " + apsLogEvent.cropName);
                updateView();
                return null;
            }
        });

        try {
            updateView();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Error : "+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        APS.unregisterToAPSEvent(this, eventReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connectOrDisconnect:
                doDisconnect();
                break;
            case R.id.action_about:
                doLaunchAbout();
                break;
            case R.id.action_settings:
                doLaunchSettings();
                break;
            case R.id.action_privacy:
                doLaunchPrivacy();
                break;
        }
        return true;
    }

    private void updateView() throws Exception {

        final List<Card> cards = new ArrayList<>();

        final List<String> cropIds = APS.getInstalledCrop(getBaseContext());

        if (cropIds.isEmpty()){
            return;
        }

        findViewById(R.id.home_empty_list_text).setVisibility(View.INVISIBLE);
        findViewById(R.id.home_empty_list).setBackgroundColor(Color.parseColor("#DEDEDE"));

        for (final String cropId : cropIds){

            try {

                final CardCrop cropCard = new CardCrop(getBaseContext(),APS.getCropDescription(getBaseContext(),cropId));
                cropCard.init();
                cards.add(cropCard);
                cropCard.onItemClick(new Callable<Void, CardWithList.ListObject>() {
                    @Override
                    public Void call(CardWithList.ListObject listObject) throws Exception {

                        final Intent intent = new Intent(getBaseContext(),ExperimentDetailsActivity.class);
                        intent.putExtra("experiment",cropId);
                        startActivity(intent);
                        return null;
                    }
                });

                final CardArrayAdapter adapter = new CardArrayAdapter(getBaseContext(), cards);
                final CardListView listView = (CardListView) findViewById(R.id.home_cards_list);
                listView.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();
               Log.e(TAG,e.getMessage());
            }
        }

    }

    private void updateProfile(){
        String username = getString(R.string.user_identity, getString(R.string.anonymous_user));
        if (isUserAuthenticated()) {
            try {
                username = String.format(getString(R.string.user_identity), APS.getUsername(this));
                final String uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
                if (username.matches("anonymous-" + uuidRegex)){
                    username = "Anonymous";
                };
            } catch (APS.SDKNotInitializedException e) {
                e.printStackTrace();
            }
        }

        TextView user_identity = (TextView) findViewById(R.id.home_user_identity);
        user_identity.setText(username);
    }

    private boolean isUserAuthenticated() {
        boolean response;
        try {
            response = APS.isConnected(this);
        } catch (APS.SDKNotInitializedException e) {
            e.printStackTrace();
            return false;
        }
        return response;
    }

    public void doLaunchSettings(){
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void doLaunchPrivacy(){
        Intent privacyIntent = new Intent(this, PrivacyActivity.class);
        startActivity(privacyIntent);
    }

    private void doDisconnect() {
        signOut = new SignOutTask(this, new SignedOutCallback());
        signOut.execute();
    }

    private void doLaunchAbout() {
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        startActivity(aboutIntent);
    }

    public void doGoToStore(View storeButton) {
        Intent storeIntent = new Intent(this, StoreActivity.class);
        startActivity(storeIntent);
    }

    public void doGoToProfil(View personalInformation) {
        if (!isUserAuthenticated()) {
            Intent slideIntent = new Intent(this, LauncherActivity.class);
            startActivity(slideIntent);
            finish();


        } else {
            // Go to profil activity
        }
    }


    private class OpenCropDetailsListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), ExperimentDetailsActivity.class);

            APSLocalCrop exp = (APSLocalCrop) parent.getAdapter().getItem(position);
            intent.putExtra("experiment", exp.getName());
            startActivity(intent);
        }
    }

    public class SignedOutCallback implements Callback<Void> {
        @Override
        public void onCall(Void aVoid) {
            signOut = null;
            Toast.makeText(getApplicationContext(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), LauncherActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(Throwable throwable) {
            signOut = null;
        }
    }
}
