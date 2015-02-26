package com.apisense.bee.games.action.subscribe;

import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.games.action.GameAchievement;
import com.google.android.gms.games.achievement.Achievement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.asl.utils.Log;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Created by Warnant on 19-02-15.
 */
public class CrowdSensingPartnerAchievement extends GameAchievement implements MissionSuscribeAchievement {

    public static final int NUMBER_MISSION_REQUIRED = 3;

    public CrowdSensingPartnerAchievement(Achievement achievement) {
        super(achievement);
    }

    @Override
    public boolean process() {

        List<Experiment> experiments = BeeGameManager.getInstance().getCurrentExperiments();
        Log.getInstance().i("CrowdSensingPartnerAchievement", "size=" + BeeGameManager.getInstance().getCurrentExperiments().size());

        Map<String, Integer> authors = new HashMap<>();

        for (Experiment e : experiments) {
            if (!authors.containsKey(e.collector)) {
                authors.put(e.collector, 0);
            }
            Integer count = authors.get(e.collector);
            authors.put(e.collector, count + 1);
        }

        for (Map.Entry<String, Integer> entry : authors.entrySet()) {
            if (entry.getValue() >= NUMBER_MISSION_REQUIRED) {
                return true;
            }
        }

        return false;
    }

    @Override
    public long getPoints() {
        return 4 * super.getPoints();
    }

    @Override
    public int getScore() {
        return 1;
    }
}
