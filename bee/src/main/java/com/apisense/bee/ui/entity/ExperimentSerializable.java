package com.apisense.bee.ui.entity;

import fr.inria.bsense.appmodel.Experiment;
import org.json.JSONException;

import java.io.Serializable;

public class ExperimentSerializable implements Serializable {

    private String name;
    private String niceName;
    private String description;
    private String copyright;
    private String type;
    private String version;
    private String main;
    private String organization;
    private String orgDescription;
    private String id;
    private String language;
    private String collector;
    private String baseUrl;
    private String remoteState;
    private String userId;
    private String path;
    private Boolean state;
    private Boolean storedState;
    private String json;

    public ExperimentSerializable(Experiment exp) {
        name = exp.name;
        niceName = exp.niceName;
        description = exp.description;
        copyright = exp.copyright;
        type = exp.type;
        version = exp.version;
        main = exp.main;
        organization = exp.organization;
        orgDescription = exp.orgDescription;
        id = exp.id;
        language = exp.language;
        collector = exp.collector;
        baseUrl = exp.baseUrl;
        remoteState = exp.remoteState;
        userId = exp.userId;
        path = exp.path;
        state = exp.state;
        storedState = exp.storedState;
        json = exp.json;
    }

    public String toString() {
        return "Name : " + niceName + "\n" +
                "Organization : " + organization + "\n" +
                "Copyright : " + copyright + "\n" +
                "Version : " + version;
    }

    public String getName() {
        return name;
    }

    public String getNiceName() {
        return niceName;
    }

    public String getDescription() {
        return description;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public String getMain() {
        return main;
    }

    public String getOrganization() {
        return organization;
    }

    public String getOrgDescription() {
        return orgDescription;
    }

    public String getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public String getCollector() {
        return collector;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getRemoteState() {
        return remoteState;
    }

    public String getUserId() {
        return userId;
    }

    public String getPath() {
        return path;
    }

    public Boolean getState() {
        return state;
    }

    public Boolean getStoredState() {
        return storedState;
    }

    public String getJson() {
        return json;
    }
}
