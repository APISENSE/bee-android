package com.apisense.bee.games.event;

import com.apisense.bee.games.utils.BaseGameActivity;

/**
 * This class is used to describe a game event which is specialized in the application sharing event
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public class ShareEvent extends GameEvent {

    /**
     * @see com.apisense.bee.games.event.GameEvent
     */
    public ShareEvent(BaseGameActivity source) {
        super(source);
    }
}
