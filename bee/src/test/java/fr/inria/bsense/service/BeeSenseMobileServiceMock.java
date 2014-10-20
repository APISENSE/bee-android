package fr.inria.bsense.service;

import fr.inria.bsense.appmodel.Experiment;

public class BeeSenseMobileServiceMock extends BSenseMobileService {

    public BeeSenseMobileServiceMock(BeeSenseServiceManager context) {
        super(context);
    }

    @Override
    public void stopExperiment(final Experiment experiment, int exitCode) throws Exception{
        if (experiment.state) {
            experiment.state = false;
        } else{
            throw new Exception("[MOCK] Experiment already stopped");
        }
    }

    @Override
    public void startExperiment(final Experiment experiment) throws Exception{
        if (! experiment.state) {
            experiment.state = true;
        } else {
            throw new Exception("[MOCK] Experiment already started");
        }
    }
}
