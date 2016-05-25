package com.apisense.bee.games;

import android.net.Uri;

public class BeePlayer {
    public final String username;
    public final Uri userImage;

    public BeePlayer(String username, Uri userImage) {
        this.username = username;
        this.userImage = userImage;
    }
}
