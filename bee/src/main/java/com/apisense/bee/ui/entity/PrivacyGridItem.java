package com.apisense.bee.ui.entity;

public class PrivacyGridItem {

    public String name = "";
    public int imgOn;
    public int imgOff;
    public boolean isActivated;

    public PrivacyGridItem(String name, int imgOn, int imgOff, boolean isActivated) {
        this.name = name;
        this.imgOn = imgOn;
        this.imgOff = imgOff;
        this.isActivated = isActivated;
    }
}
