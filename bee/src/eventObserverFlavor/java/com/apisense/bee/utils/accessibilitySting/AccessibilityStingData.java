package com.apisense.bee.utils.accessibilitySting;

import com.apisense.bee.utils.accessibilitySting.AccessibilityEventWrapper;
import io.apisense.dart.lib.DataImpl;

public final class AccessibilityStingData extends DataImpl {
  public final AccessibilityEventWrapper AccessibilityEvent;

  public AccessibilityStingData(final int seeds, final AccessibilityEventWrapper AccessibilityEvent) {
    super(seeds);
    this.AccessibilityEvent = AccessibilityEvent;
  }
}
