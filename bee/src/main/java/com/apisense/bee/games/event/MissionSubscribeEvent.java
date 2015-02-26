package com.apisense.bee.games.event;

import com.apisense.bee.games.utils.BaseGameActivity;

import java.util.List;

import fr.inria.bsense.appmodel.Experiment;

/**
 * Created by Warnant on 19-02-15.
 */
public class MissionSubscribeEvent extends GameEvent {

    private List<Experiment> experiments;

    public MissionSubscribeEvent(BaseGameActivity source) {
        super(source);
    }

    public List<Experiment> getExperiments() {
        return this.experiments;
    }
}
