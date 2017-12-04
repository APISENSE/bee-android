package com.apisense.bee.utils.accessibilitySting;

import java.util.Set;

import io.apisense.dart.api.Seed;
import io.apisense.dart.api.Token;
import io.apisense.dart.lib.DartImpl;
import io.apisense.dart.lib.EventBus;
import io.apisense.dart.lib.Tokens;
import io.apisense.dart.lib.Tokens.TokensListener;
import io.apisense.dart.lib.events.EventListenerTriggered;

public abstract class AccessibilityStingDartSkel extends DartImpl<AccessibilityStingData> implements AccessibilityStingDart {
  private final EventBus bus;

  private Tokens<Void> accessibilityEventTriggered;

  protected AccessibilityStingDartSkel(final EventBus bus, final Set<? extends Seed> fields) {
    super(AccessibilityStingDart.class, fields);
    this.bus = bus;
    this.accessibilityEventTriggered = new Tokens<>(bus, initAccessibilityEventTriggeredListener());
  }

  /**
   * Information about the event */
  public AccessibilityEventWrapper AccessibilityEvent() {
    return null;
  }

  protected abstract TokensListener<Void> initAccessibilityEventTriggeredListener();

  /**
   * Send the accessibility event */
  public Token onAccessibilityEventTriggered(Object callback) {
    return this.accessibilityEventTriggered.register(callback, null);
  }

  @Override
  public AccessibilityStingData map(final int mask) {
    return new AccessibilityStingData(mask, AccessibilityStingSeed.ACCESSIBILITYEVENT.matches(mask) ? this.AccessibilityEvent() : null);
  }

  protected final void publish(final AccessibilityStingEvent event, final AccessibilityStingData dataClass) {
    this.bus.publish(EventListenerTriggered.build(event.toString(), dataClass, null));
  }

  public void stop() {
    this.accessibilityEventTriggered.stop();
  }
}
