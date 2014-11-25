package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.apisense.android.APSApplication;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSRequest;
import com.apisense.api.Callback;
import com.apisense.api.Log;


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

                // SDK is ready
                if (APS.isConnected(getBaseContext())){
                    // user is already connected
                    //  TODO launch crop activity

                }else{


                    // start login activity
                    startActivityForResult(new Intent(getBaseContext(), SlideshowActivity.class), LOGIN_REQUEST);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(throwable);
            }
        });
    }

    private void handlerRequest(final APSRequest request, final int page){

        request.runCallbackOnUIThread(this);
        request.setCallback(new Callback() {
            @Override
            public void onCall(Object ignored) throws Exception {

                // user is connected
                // TODO launch crop activity
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(throwable);
                Toast.makeText(getBaseContext(),"Error : "+throwable.getMessage(),Toast.LENGTH_LONG).show();


                final Intent intent = new Intent(getBaseContext(), SlideshowActivity.class);
                intent.putExtra("goTo",page);

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
                    if (action.equals(SlideshowActivity.LOGIN_ACTION)) {
                        // connection user with credential

                        final APSRequest request = APS.connect(
                                getBaseContext(),
                                data.getStringExtra(SlideshowActivity.LOGIN_PSEUDO),
                                data.getStringExtra(SlideshowActivity.LOGIN_PWD));

                        handlerRequest(request,SlideshowActivity.SIGNIN);

                    } else if (action.equals(SlideshowActivity.LOGIN_ANONYMOUS_ACTION)) {
                        //create an anonymous connection

                        final APSRequest request = APS.ensureAnonymousConnection(getBaseContext());
                        handlerRequest(request,SlideshowActivity.SIGNIN);

                    } else if (action.equals(SlideshowActivity.REGISTER_ACTION)) {

                        // create a new user account
                        final APSRequest request = APS.createAccount(
                                getBaseContext(),
                                data.getStringExtra(SlideshowActivity.REGISTER_PSEUDO),
                                data.getStringExtra(SlideshowActivity.REGISTER_PSEUDO),
                                data.getStringExtra(SlideshowActivity.REGISTER_PWD),
                                "default@apisense.com");

                        handlerRequest(request,SlideshowActivity.SIGNIN);
                    }

                } catch(Throwable e){
                    Log.e(e);
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
