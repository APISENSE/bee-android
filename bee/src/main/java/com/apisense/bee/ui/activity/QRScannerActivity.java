package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class QRScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    private static final String TAG = "QRScannerActivity";
    static final int INSTALL_FROM_QR = 0;
    static final String CROP_ID_KEYWORD = "crop_id";
    private ZBarScannerView scannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        scannerView = new ZBarScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String cropID = result.getContents();
        Log.d(TAG, "Got value in QR code: " + cropID);
        Intent data = new Intent();
        data.putExtra(CROP_ID_KEYWORD, cropID);
        setResult(RESULT_OK, data);
        finish();
    }
}
