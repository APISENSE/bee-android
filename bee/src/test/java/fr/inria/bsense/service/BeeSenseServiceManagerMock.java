package fr.inria.bsense.service;

public class BeeSenseServiceManagerMock extends BeeSenseServiceManager {
    @Override
    public BSenseMobileService getBSenseMobileService(){
        return new BeeSenseMobileServiceMock(this);
    }

}
