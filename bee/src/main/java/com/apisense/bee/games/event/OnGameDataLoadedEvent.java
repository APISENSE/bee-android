package com.apisense.bee.games.event;

import com.apisense.bee.games.utils.BaseGameActivity;

/**
 * This class is used to describe a game event which is specialized in the Bee mission event
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public class OnGameDataLoadedEvent extends GameEvent {

    /**
     * @see com.apisense.bee.games.event.GameEvent
     */
    public OnGameDataLoadedEvent(BaseGameActivity source) {
        super(source);
    }
}
