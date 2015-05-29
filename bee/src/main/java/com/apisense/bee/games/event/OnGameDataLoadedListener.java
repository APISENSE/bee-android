package com.apisense.bee.games.event;

/**
 * This interface describes all methods for the GameDataLoaded event listener
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public interface OnGameDataLoadedListener {

    /**
     * This method is fired when a new GameDataLoaded event is detected by the system
     *
     * @param event OnGameDataLoadedEvent the event fired
     * @see com.apisense.bee.games.event.OnGameDataLoadedEvent
     */
    void onRefresh(OnGameDataLoadedEvent event);

}
