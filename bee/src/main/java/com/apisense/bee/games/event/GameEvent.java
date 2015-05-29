package com.apisense.bee.games.event;

import com.apisense.bee.games.utils.BaseGameActivity;

/**
 * This class is used to describe a game event for the game manager
 * All the implementation of the game module is based on the event driven programming model
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public abstract class GameEvent {

    protected BaseGameActivity source;

    /**
     * Constructor
     *
     * @param source BeeGameActivity the activity which has created the current event
     */
    public GameEvent(BaseGameActivity source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "GameEvent [Source= " + this.source.getClass().getName() + "]";
    }
}
