package com.apisense.bee.games.event;

import com.apisense.bee.games.utils.BaseGameActivity;

/**
 * Created by Warnant on 19-02-15.
 */
public abstract class GameEvent {

    protected BaseGameActivity source;


    public GameEvent(BaseGameActivity source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "GameEvent [Source= " + this.source.getClass().getName() + "]";
    }
}
