package com.apisense.bee.ui.entity;

import fr.inria.bsense.appmodel.Experiment;

import java.io.Serializable;

public class ExperimentSerializable implements Serializable {
    private String name;

    public ExperimentSerializable(Experiment exp) {
        name = exp.name;
    }

    public String getName() {
        return name;
    }
}
