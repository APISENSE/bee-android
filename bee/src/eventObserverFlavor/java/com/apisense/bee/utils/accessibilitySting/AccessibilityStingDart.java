package com.apisense.bee.utils.accessibilitySting;

import com.apisense.bee.utils.accessibilitySting.AccessibilityEventWrapper;
import io.apisense.dart.api.Dart;
import io.apisense.dart.api.Token;

/**
 * Retrieve data about the user accessibilitySting.
 */
public interface AccessibilityStingDart extends Dart<AccessibilityStingData> {
  /**
   * identifier of the sting to be used as require('accessibilitySting').
   */
  String NAME = "accessibilitySting";

  /**
   * Return the current AccessibilityEvent of the accessibilitySting.
   *
   * @return The accessibilitySting AccessibilityEvent.
   */
  AccessibilityEventWrapper AccessibilityEvent();

  /**
   * Execute the callback when the accessibilitySting dart detects a accessibilityEventTriggered.
   *
   * @param callback The action to process when done.
   * @return The cancellation Token.
   */
  Token onAccessibilityEventTriggered(Object callback);
}
