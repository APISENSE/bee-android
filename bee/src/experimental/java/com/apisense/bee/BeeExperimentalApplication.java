package com.apisense.bee;

import com.apisense.bee.utils.accessibilitySting.AccessibilitySting;

import java.util.Collections;
import java.util.List;

import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.sting.InjectedStingPackage;
import io.apisense.sdk.core.sting.StingComponent;
import io.apisense.sting.lib.Sting;
import io.apisense.sting.netsense.NetsenseStingModule;

/**
 * Created by Mohammad Naseri
 */

public class BeeExperimentalApplication extends BeeApplication {
    @Override
    public void configure(APISENSE apisense) {
        apisense.bindStingPackage(new InjectedStingPackage() {
            @Override
            protected List<Sting> getInstances(StingComponent stingComponent) {
                return Collections.<Sting>singletonList(new AccessibilitySting(stingComponent.bus()));
            }
        }, new NetsenseStingModule());
    }
}
