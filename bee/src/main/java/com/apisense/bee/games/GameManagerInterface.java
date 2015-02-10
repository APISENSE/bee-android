package com.apisense.bee.games;


import android.app.Activity;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;

public interface GameManagerInterface extends GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public boolean initialize(Activity context);
    public boolean connect();
    public boolean disconnect();
}

