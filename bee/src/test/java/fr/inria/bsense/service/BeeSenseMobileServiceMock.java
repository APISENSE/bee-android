package fr.inria.bsense.service;

import fr.inria.bsense.appmodel.Experiment;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class BeeSenseMobileServiceMock extends BSenseMobileService {

    public BeeSenseMobileServiceMock(BeeSenseServiceManager context) {
        super(context);
    }

    public static Map<String, Experiment> installedExperiments = new HashMap<String, Experiment>();

    @Override
    public void stopExperiment(final Experiment experiment, int exitCode) throws Exception {
        if (experiment.state) {
            experiment.state = false;
        } else {
            throw new Exception("[MOCK] Experiment already stopped");
        }
    }

    @Override
    public void startExperiment(final Experiment experiment) throws Exception {
        if (!experiment.state) {
            experiment.state = true;
        } else {
            throw new Exception("[MOCK] Experiment already started");
        }
    }

    @Override
    public java.util.Map<String, Experiment> getInstalledExperiments() {
        return BeeSenseMobileServiceMock.installedExperiments;
    }

    @Override
    public void sendTrack(final Experiment experiment) throws Exception {
    }

    @Override
    public Experiment getExperiment(final String experimentName) throws JSONException {
        Experiment exp = null;
        if (installedExperiments.containsKey(experimentName)) {
            exp = installedExperiments.get(experimentName);
        }
        return exp;
    }

    @Override
    public void uninstallExperiment(final Experiment experiment) throws Exception {
    }

    @Override
    public void installExperiment(final Experiment experiment) throws Exception {
    }
}
