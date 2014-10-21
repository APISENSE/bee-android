package fr.inria.bsense.service;

public class BSenseServerServiceMock extends BSenseServerService {
    public BSenseServerServiceMock(BeeSenseServiceManagerMock context) {
        super(context);
    }

    @Override
    public Boolean isConnected(){
        return true;
    }

}
