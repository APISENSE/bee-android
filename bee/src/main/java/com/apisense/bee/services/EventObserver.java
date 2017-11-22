package com.apisense.bee.services;

import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.apisense.bee.utils.accessibilitySting.AccessibilityEventWrapper;

import java.util.Stack;

import io.apisense.sting.phone.system.WindowChangeDetectingService;

/**
 * Created by Mohammad Naseri
 */

public class EventObserver extends WindowChangeDetectingService {
    private static final String TAG = EventObserver.class.getName();

    private static OnAccessibilityEvent callback;
    private static Stack<StackElement> events = new Stack<>();
    private static StackElement lastEvent = null;
    private static String tempInput = "";
    private static AccessibilityEventWrapper tempWrapper;

    private static String EDIT_TEXT_CLASS = "android.widget.EditText";
    private static String WIDGET_CLASS = "android.widget";

    @Override
    public void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");

        long t_start = SystemClock.uptimeMillis();
        Log.d(TAG, "EXECUTION START-TIME: " + t_start);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);


        if (events.size() > 0)
            lastEvent = events.lastElement();

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

            if (!tempInput.isEmpty()) {
//                    Log.d(TAG, "onAccessibilityEvent: " + tempWrapper.toString());
                if (callback != null) {
                    callback.sendData(tempWrapper);
                }
            }

            if (!event.isPassword()) {
                if (event.getClassName().equals(EDIT_TEXT_CLASS))
                    if (!event.getText().isEmpty())
                        tempInput = String.valueOf(event.getText().get(0));
                    else
                        tempInput = "";
            } else {
                tempInput = "";
            }

        }

        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            if (!tempInput.equals("")) {
//                    Log.d(TAG, "onAccessibilityEvent: " + tempWrapper.toString());
                if (callback != null) {
                    callback.sendData(tempWrapper);
                }
                AccessibilityEventWrapper accessibilityEventWrapper = new AccessibilityEventWrapper(event, null);
//                    Log.d(TAG, "onAccessibilityEvent: " + accessibilityEventWrapper.toString());
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

        if (event.getText().size() != 0 && event.getClassName() != null && tempInput.isEmpty()
                && !event.getClassName().toString().equals(EDIT_TEXT_CLASS)
                && event.getClassName().toString().contains(WIDGET_CLASS)) {
            AccessibilityEventWrapper accessibilityEventWrapper = new AccessibilityEventWrapper(event, null);
//                Log.d(TAG, "onAccessibilityEvent: " + accessibilityEventWrapper.toString());
            if (callback != null) {
                callback.sendData(accessibilityEventWrapper);
            }
            tempInput = "";
        }


        if (event.getEventType() != 0) {
            String packageName = "";
            if (event.getPackageName() != null) {
                packageName = event.getPackageName().toString();
            }
            events.push(new StackElement(AccessibilityEvent.eventTypeToString(event.getEventType()), packageName));
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


    static class StackElement {
        StackElement(String eventType, String eventPackage) {
            this.eventType = eventType;
            this.eventPackage = eventPackage;
        }

        public final String eventType;
        public final String eventPackage;
    }
}
