package fr.inria.bsense.service;

import fr.inria.bsense.appmodel.Experiment;

import java.util.ArrayList;
import java.util.List;

public class BeeSenseServerServiceMock extends BSenseServerService {

    public static List<Experiment> availableExperiments = new ArrayList<Experiment>();

    public BeeSenseServerServiceMock(BeeSenseServiceManagerMock context) {
        super(context);
    }

    @Override
    public Boolean isConnected(){
        return true;
    }

    @Override
    public void searchRemoteExperiment(String index, String limit) throws Exception {
        if (Integer.valueOf(index) < 0){
            throw new Exception("[Mock] searchRemoteException failing");
        }
    }

    public List<Experiment> getRemoteExperiments() {
        return BeeSenseServerServiceMock.availableExperiments;
    }

    @Override
    public void subscribeExperiment(final Experiment experiment) throws Exception{
    }

    @Override
    public void unsubscribeExperiment(final Experiment experiment) throws Exception{
    }
}
