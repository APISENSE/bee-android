package com.apisense.bee.games.action;

/**
 * Created by Warnant on 12-02-15.
 */
public abstract class GameAchievement implements GameAction {

    protected String id;
    protected String name;
    protected boolean incremental;
    protected int incrementPart;

    public GameAchievement(String id, boolean incremental) {
        this.id = id;
        this.incremental = incremental;
    }

    public boolean isIncremental() {
        return this.incremental;
    }

    public String getId() {
        return this.id;
    }

    public int getIncrementPart() {
        return this.incrementPart;
    }

    public void setIncrementPart(int incrementPart) {
        this.incrementPart = incrementPart;
    }

    @Override
    public String toString() {
        return "id=" + this.id + ", name=" + this.name + ", isIncremental=" + this.incremental;
    }
}
