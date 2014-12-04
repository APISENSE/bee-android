package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.apisense.android.APSApplication;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSRequest;
import com.apisense.core.api.APSException;
import com.apisense.core.api.Callback;
import com.apisense.core.api.Log;


public class LauncherActivity extends Activity {
    private final String TAG = "LauncherActivity";

    private static final int LOGIN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup APISENSE SDK
        APS.ready((APSApplication) getApplicationContext(), new Callback<Void>() {
            @Override
            public void onCall(Void aVoid) throws Exception {
                if (APS.isConnected(getBaseContext())) {

                    startActivity(new Intent(getBaseContext(), HomeActivityBis.class));
                    finish();

                } else {
                    startActivityForResult(new Intent(getBaseContext(), SlideshowActivity.class), LOGIN_REQUEST);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(throwable);
            }
        });
    }

    private void handlerRequest(final APSRequest request){
        request.runCallbackOnUIThread(this);
        request.setCallback(new Callback() {
            @Override
            public void onCall(Object ignored) throws Exception {
                startActivity(new Intent(getBaseContext(), HomeActivityBis.class));
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(throwable);
                Toast.makeText(getBaseContext(), "Error : " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                final Intent intent = new Intent(getBaseContext(), SlideshowActivity.class);
                intent.putExtra("goTo", SlideshowActivity.SIGNIN);
                startActivityForResult(intent, LOGIN_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST){
            if (resultCode == RESULT_OK){
                try {
                    final String action = data.getStringExtra(SlideshowActivity.KEY_AUTHENTICATION_ACTION);
                    APSRequest request = null;

                    if (action.equals(SlideshowActivity.LOGIN_ACTION)) {
                        request = generateLoginRequest(data);
                    } else if (action.equals(SlideshowActivity.LOGIN_ANONYMOUS_ACTION)) {
                        request = generateAnonymousLoginRequest();
                    } else if (action.equals(SlideshowActivity.REGISTER_ACTION)) {
                        request = generateRegistrationRequest(data);
                    }
                    handlerRequest(request);
                } catch(Throwable e){
                    Log.e(e);
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private APSRequest generateLoginRequest(Intent data) throws APSException {
        return APS.connect(getBaseContext(), data.getStringExtra(SlideshowActivity.LOGIN_PSEUDO),
                data.getStringExtra(SlideshowActivity.LOGIN_PWD));
    }

    private APSRequest generateAnonymousLoginRequest() throws APSException {
        return APS.ensureAnonymousConnection(getBaseContext());
    }

    private APSRequest generateRegistrationRequest(Intent data) throws APSException {
         return APS.createAccount(getBaseContext(),
                data.getStringExtra(SlideshowActivity.REGISTER_PSEUDO),
                data.getStringExtra(SlideshowActivity.REGISTER_PSEUDO),
                data.getStringExtra(SlideshowActivity.REGISTER_PWD),
                "default@apisense.com");
    }
}
