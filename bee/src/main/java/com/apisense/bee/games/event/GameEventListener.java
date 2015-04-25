package com.apisense.bee.games.event;

/**
 * This interface describes all methods for the GameEvent event listener
 *
 * @author Quentin Warnant
 * @version 1.0
 */
public interface GameEventListener {

    /**
     * This method is fired when a new GameEvent event is detected by the system
     *
     * @param gameEvent GameEvent the event fired
     * @see com.apisense.bee.games.event.GameEvent
     */
    void fireGameEventPerformed(GameEvent gameEvent);

}
