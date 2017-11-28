package com.apisense.bee.utils.accessibilitySting;

import java.util.EnumSet;

import io.apisense.dart.lib.BitMask;

public enum AccessibilityStingEvent {
  ACCESSIBILITYEVENT_TRIGGERED("accessibilitysting:accessibilityEvent:triggered");

  private final int value;

  private final String label;

  AccessibilityStingEvent(final String label) {
    this.value = 1 << this.ordinal();
    this.label = label;
  }

  public int value() {
    return this.value;
  }

  public final boolean matches(final int value) {
    return BitMask.matches(this.value, value);
  }

  public final boolean equals(final String label) {
    return this.label.equalsIgnoreCase(label);
  }

  public final String toString() {
    return this.label;
  }

  public static final EnumSet<AccessibilityStingEvent> parse(final String... labels) {
    EnumSet<AccessibilityStingEvent> res = EnumSet.noneOf(AccessibilityStingEvent.class);
    for (String label : labels) {
      for (AccessibilityStingEvent val : values()) {
        if (val.equals(label)) {
          res.add(val);
        }
      }
    }
    return res;
  }

  public static final EnumSet<AccessibilityStingEvent> parse(final int... codes) {
    EnumSet<AccessibilityStingEvent> res = EnumSet.noneOf(AccessibilityStingEvent.class);
    for (int code : codes) {
      for (AccessibilityStingEvent val : values()) {
        if (val.matches(code)) {
          res.add(val);
        }
      }
    }
    return res;
  }
}
