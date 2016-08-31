package com.apisense.bee.callbacks;

import android.os.Bundle;
import android.util.Log;

import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.bee.LoginProvider;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.rollbar.android.Rollbar;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookLoginCallback implements FacebookCallback<LoginResult> {
    private static final String TAG = "FacebookCallback";
    private static final String EMAIL_FIELD = "email";
    private final APISENSE.Sdk apisenseSdk;
    private final APSCallback<Void> onLoggedIn;

    public FacebookLoginCallback(APISENSE.Sdk apisenseSdk, APSCallback<Void> onLoggedIn) {
        this.apisenseSdk = apisenseSdk;
        this.onLoggedIn = onLoggedIn;
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        final AccessToken accessToken = loginResult.getAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String email = object.getString(EMAIL_FIELD);
                    apisenseSdk.getSessionManager().login(email, accessToken.getToken(),
                            LoginProvider.FACEBOOK, onLoggedIn);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", EMAIL_FIELD);
        request.setParameters(parameters);
        request.executeAsync();

    }

    @Override
    public void onCancel() {
        Log.e(TAG, "Facebook login canceled");
    }

    @Override
    public void onError(FacebookException exception) {
        Rollbar.reportException(exception);
        Log.e(TAG, "Error while connecting to facebook", exception);
    }
}
