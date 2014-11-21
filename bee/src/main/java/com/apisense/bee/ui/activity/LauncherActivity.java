package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LauncherActivity extends Activity {
    private final String TAG = "LAuncherActivity";

    private static final int LOGIN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SlideshowActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST);
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST){
            if (resultCode == RESULT_OK){
                for (String key:data.getExtras().keySet() )
                Log.i(TAG, key);
            }
        }
    }
}
