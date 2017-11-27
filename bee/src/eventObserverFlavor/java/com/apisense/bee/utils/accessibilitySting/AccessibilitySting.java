package com.apisense.bee.utils.accessibilitySting;

import java.util.EnumSet;
import javax.inject.Inject;

import com.apisense.bee.R;
import com.apisense.bee.services.EventObserver;

import io.apisense.dart.lib.EventBus;
import io.apisense.dart.lib.EventFilter;
import io.apisense.dart.lib.Tokens;
import io.apisense.sting.lib.Sensor;

/**
 * Created by Mohammad Naseri
 */

public class AccessibilitySting extends AccessibilityStingDartSkel {
    public static final Sensor SENSOR_DESCRIPTION = new Sensor(
            "Event Observer",
            NAME,
            "Gives info about the events, including user inputs such as password.",
            R.drawable.ic_accessiblity_sting
    );
    @Inject
    public AccessibilitySting(EventBus bus) {
        super(bus, EnumSet.allOf(AccessibilityStingSeed.class));
    }

    @Override
    protected Tokens.TokensListener<Void> initAccessibilityEventTriggeredListener() {
        return new Tokens.TokensListener<Void>() {
            private EventObserver.OnAccessibilityEvent callback;

            @Override
            public void init() {

            }

            @Override
            public String computeTopic(EventFilter<Void> eventFilter) {
                if (callback == null) {
                    callback = new EventObserver.OnAccessibilityEvent() {
                        @Override
                        public void sendData(AccessibilityEventWrapper event) {
                            publish(AccessibilityStingEvent.ACCESSIBILITYEVENT_TRIGGERED, new AccessibilityStingData(seeds(), event));
                        }
                    };
                    EventObserver.createCallback(callback);
                }
                return AccessibilityStingEvent.ACCESSIBILITYEVENT_TRIGGERED.toString();
            }

            @Override
            public void discardFilter(EventFilter<Void> eventFilter) {

            }

            @Override
            public void release() {

            }
        };
    }

}
