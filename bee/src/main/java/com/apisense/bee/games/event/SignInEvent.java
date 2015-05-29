package com.apisense.bee.games.event;

import com.apisense.bee.games.utils.BaseGameActivity;

/**
 * This class is used to describe a game event which is specialized in the app connection event
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public class SignInEvent extends GameEvent {

    /**
     * @see com.apisense.bee.games.event.GameEvent
     */
    public SignInEvent(BaseGameActivity source) {
        super(source);
    }
}
