package com.apisense.bee.ui.adapter;

import com.google.android.gms.maps.GoogleMap;
import org.json.JSONObject;

public class GoogleMapAdapter {

    private GoogleMap mGoogleMap;

    public GoogleMapAdapter(GoogleMap map) {
        mGoogleMap = map;
    }

    public void setDataSet(JSONObject json) {

    }

    public void addAll() {

    }

    public void clearMap() {
        mGoogleMap.clear();
    }
}
