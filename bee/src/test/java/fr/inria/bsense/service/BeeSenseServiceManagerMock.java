package fr.inria.bsense.service;

public class BeeSenseServiceManagerMock extends BeeSenseServiceManager {
    @Override
    public BSenseMobileService getBSenseMobileService(){
        return new BeeSenseMobileServiceMock(this);
    }
    @Override
    public BSenseServerService getBSenseServerService() { return new BSenseServerServiceMock(this); }
}
