package com.apisense.bee.utils.accessibilitySting;

import android.view.accessibility.AccessibilityEvent;

import java.util.Date;


/**
 * Created by mnaseri on 11/15/17.
 */

public class AccessibilityEventWrapper {
    public final String eventType;
    public final long eventTime;
    public final String packageName;
    public final String className;
    public final String text;
    public final String contentDescription;
    public final boolean isPassword;
    public final boolean isChecked;
    public final boolean isFullScreen;


    public AccessibilityEventWrapper(AccessibilityEvent event, String text) {
        this.eventType = AccessibilityEvent.eventTypeToString(event.getEventType());
        this.eventTime = event.getEventTime();
        this.packageName = String.valueOf(event.getPackageName());
        this.className = String.valueOf(event.getClassName());
        if (!event.getText().isEmpty())
            this.text = text == null ? String.valueOf(event.getText().get(0)) : text;
        else
            this.text = null;
        this.contentDescription = String.valueOf(event.getContentDescription());
        this.isPassword = event.isPassword();
        this.isChecked = event.isChecked();
        this.isFullScreen = event.isFullScreen();
    }

    public String toString() {
        Date d = new Date(eventTime);
        return " packageName:" + packageName + " eventType:" + eventType + " eventTime:" + d.toString() + " className:" + className + " text:" + text + " isPassword:" +
                isPassword + " isChecked:" + isChecked + " isFullScreen:" + isFullScreen + " contentDescription:" + contentDescription;
    }

}
