package fr.inria.bsense.service;

import fr.inria.bsense.appmodel.Experiment;

import java.util.ArrayList;
import java.util.List;

public class BeeSenseServerServiceMock extends BSenseServerService {

    public static List<Experiment> availableExperiments = new ArrayList<Experiment>();

    public static boolean userConnected = false;
    public static boolean accountCreated = false;
    public static boolean accountUpdated = false;

    private static List<String> existingAccount = new ArrayList<String>();

    public BeeSenseServerServiceMock(BeeSenseServiceManagerMock context) {
        super(context);
    }

    public static void mockClearAccounts() {
        existingAccount.clear();
    }

    @Override
    public Boolean isConnected() {
        return userConnected;
    }

    @Override
    public void searchRemoteExperiment(String index, String limit) throws Exception {
        if (Integer.valueOf(index) < 0) {
            throw new Exception("[Mock] searchRemoteException failing");
        }
    }

    public List<Experiment> getRemoteExperiments() {
        return BeeSenseServerServiceMock.availableExperiments;
    }

    @Override
    public void subscribeExperiment(final Experiment experiment) throws Exception {
    }

    @Override
    public void unsubscribeExperiment(final Experiment experiment) throws Exception {
    }

    @Override
    public String createAccount(
            final String host, final String fullname,
            final String username, final String password,
            final String email
    ) throws Exception {
        if (existingAccount.contains(username)) {
            throw new Exception("[MOCK] Account already exists");
        } else {
            existingAccount.add(username);
            accountCreated = true;
        }
        return null;
    }

    @Override
    public void connect(String username, String password) throws Exception {
        userConnected = true;
    }

    @Override
    public void disconnect() {
        userConnected = false;
    }

    @Override
    public void updateUserAccount() throws Exception {
        accountUpdated = true;
    }
}
