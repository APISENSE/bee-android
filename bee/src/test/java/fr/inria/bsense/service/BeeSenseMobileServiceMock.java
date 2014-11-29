package fr.inria.bsense.service;

public class BeeSenseMobileServiceMock {

//    public static boolean tracksSent = false;
//    public static boolean experimentStopped = false;
//    public static Experiment lastUninstalledExp = null;
//    public static Experiment lastInstalledExp = null;
//
//    public BeeSenseMobileServiceMock(BeeSenseServiceManager context) {
//        super(context);
//    }
//
//    public static Map<String, Experiment> installedExperiments = new HashMap<String, Experiment>();
//
//    @Override
//    public void stopExperiment(final Experiment experiment, int exitCode) throws Exception {
//        if (experiment.state) {
//            experiment.state = false;
//        } else {
//            throw new Exception("[MOCK] Experiment already stopped");
//        }
//    }
//
//    @Override
//    public void startExperiment(final Experiment experiment) throws Exception {
//        if (!experiment.state) {
//            experiment.state = true;
//        } else {
//            throw new Exception("[MOCK] Experiment already started");
//        }
//    }
//
//    @Override
//    public java.util.Map<String, Experiment> getInstalledExperiments() {
//        return BeeSenseMobileServiceMock.installedExperiments;
//    }
//
//    @Override
//    public void sendTrack(final Experiment experiment) throws Exception {
//    }
//
//    @Override
//    public Experiment getExperiment(final String experimentName) throws JSONException {
//        Experiment exp = null;
//        if (installedExperiments.containsKey(experimentName)) {
//            exp = installedExperiments.get(experimentName);
//        }
//        return exp;
//    }
//
//    @Override
//    public void uninstallExperiment(final Experiment experiment) throws Exception {
//        lastUninstalledExp = experiment;
//    }
//
//    @Override
//    public void installExperiment(final Experiment experiment) throws Exception {
//        lastInstalledExp = experiment;
//    }
//
//    @Override
//    public void sendAllTrack() throws Exception {
//        tracksSent = true;
//    }
//
//    @Override
//    public void stopAllExperiments(int exitCode) throws Exception {
//        experimentStopped = true;
//    }
//
}
