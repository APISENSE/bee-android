package com.apisense.bee.games;

import com.apisense.bee.games.event.OnGameDataLoadedEvent;

/**
 * Created by Warnant on 17-03-15.
 */
public interface OnGameDataLoadedListener {

    void onRefresh(OnGameDataLoadedEvent event);

}
