package com.apisense.bee.utils.accessibilitySting;

import java.util.EnumSet;

import io.apisense.dart.api.Seed;
import io.apisense.dart.lib.BitMask;

public enum AccessibilityStingSeed implements Seed {
  TIMESTAMP("timestamp", Long.class),

  ACCESSIBILITYEVENT("AccessibilityEvent", AccessibilityEventWrapper.class);

  private final int value;

  private final String label;

  private final Class type;

  private AccessibilityStingSeed(final String label, final Class type) {
    this.value = 1 << this.ordinal();
    this.label = label;
    this.type = type;
  }

  public int value() {
    return this.value;
  }

  public Class type() {
    return this.type;
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

  public static final EnumSet<AccessibilityStingSeed> parse(final String... labels) {
    EnumSet<AccessibilityStingSeed> res = EnumSet.noneOf(AccessibilityStingSeed.class);
    for (String label : labels) {
      for (AccessibilityStingSeed val : values()) {
        if (val.equals(label)) {
          res.add(val);
        }
      }
    }
    return res;
  }

  public static final EnumSet<AccessibilityStingSeed> parse(final int... codes) {
    EnumSet<AccessibilityStingSeed> res = EnumSet.noneOf(AccessibilityStingSeed.class);
    for (int code : codes) {
      for (AccessibilityStingSeed val : values()) {
        if (val.matches(code)) {
          res.add(val);
        }
      }
    }
    return res;
  }
}
