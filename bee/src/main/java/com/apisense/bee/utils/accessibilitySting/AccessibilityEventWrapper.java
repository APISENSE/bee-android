package com.apisense.bee.utils.accessibilitySting;

import android.view.accessibility.AccessibilityEvent;

import java.util.Date;

/**
 * Created by mnaseri on 11/15/17.
 */

public class AccessibilityEventWrapper {
    public String EventType;
    public String EventTime;
    public String PackageName;
    public String ClassName;
    public String Text;
    public String ContentDescription;
    public boolean IsPassword;
    public boolean IsChecked;
    public boolean IsFullScreen;


    public AccessibilityEventWrapper(AccessibilityEvent event, String text) {
        this.EventType = AccessibilityEvent.eventTypeToString(event.getEventType());
        this.EventTime = String.valueOf(event.getEventTime());
        this.PackageName = String.valueOf(event.getPackageName());
        this.ClassName = String.valueOf(event.getClassName());
        if (event.getText().size() != 0)
            this.Text = text == null ? String.valueOf(event.getText().get(0)) : text;
        else
            this.Text = null;
        this.ContentDescription = String.valueOf(event.getContentDescription());
        this.IsPassword = event.isPassword();
        this.IsChecked = event.isChecked();
        this.IsFullScreen = event.isFullScreen();
    }

    public String toString() {
        Date d = new Date(Long.valueOf(EventTime));
        return " PackageName:" + PackageName + " EventType:" + EventType + " EventTime:" + d.toString() + " ClassName:" + ClassName + " Text:" + Text + " IsPassword:" +
                IsPassword + " IsChecked:" + IsChecked + " IsFullScreen:" + IsFullScreen + " ContentDescription:" + ContentDescription;
    }

}
