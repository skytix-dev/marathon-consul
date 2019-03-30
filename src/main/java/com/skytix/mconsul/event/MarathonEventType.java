package com.skytix.mconsul.event;

import com.skytix.mconsul.services.marathon.MarathonUtils;
import com.skytix.mconsul.utils.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.function.Predicate;

/**
 * Created by xfire on 8/10/2015.
 */
public enum MarathonEventType {
    STATUS_UPDATE("status_update_event", StatusUpdateEvent.class, StatusUpdateEventHandler::new, pTrue()),
    FAILED_HEALTH_CHECK("failed_health_check_event", FailedHealthCheckEvent.class, FailedHealthCheckStatusEventHandler::new, pTrue()),
    UNHEALTHY_TASK_KILL("unhealthy_task_kill_event", UnhealthyTaskKillEvent.class, UnhealthyTaskKillEventHandler::new, MarathonUtils.pLessThan(Version.of("1.4"))),
    UNHEALTHY_INSTANCE_KILL("unhealthy_instance_kill_event", UnhealthyTaskKillEvent.class, UnhealthyInstanceKillEventHandler::new, MarathonUtils.pGreaterThanOrEq(Version.of("1.4"))),
    INSTANCE_CHANGED("instance_changed_event", InstanceChangedEvent.class, InstanceChangedEventHandler::new, pFalse()),
    HEALTH_STATUS_CHANGED("health_status_changed_event", HealthStatusChangedEvent.class, HealthStatusChangedEventHandler::new, MarathonUtils.pLessThan(Version.of("1.4"))),
    INSTANCE_HEALTH_CHANGED("instance_health_changed_event", InstanceHealthChangedEvent.class, InstanceHealthChangedEventHandler::new, MarathonUtils.pGreaterThanOrEq(Version.of("1.4")));
    // Ignoring the remaining events.

    private static final Logger log = LoggerFactory.getLogger(MarathonEventType.class);

    private final String mName;
    private final Class<? extends MarathonEvent> mEventClass;
    private final EventHandlerBuilder<? extends MarathonEvent, ? extends MarathonEventHandler> mEventHandler;
    private final Predicate<Version> mSupportedPredicate;

    <E extends MarathonEvent, T extends MarathonEventHandler<E>> MarathonEventType(String aName, Class<E> aEventClass, EventHandlerBuilder<E, T> aEventHandler, Predicate<Version> aSupportedPredicate) {
        mName = aName;
        mEventClass = aEventClass;
        mEventHandler = aEventHandler;
        mSupportedPredicate = aSupportedPredicate;
    }

    public String getName() {
        return mName;
    }

    public Class<? extends MarathonEvent> getEventClass() {
        return mEventClass;
    }

    public EventHandlerBuilder getEventHandlerBuilder() {
        return mEventHandler;
    }

    public Predicate<Version> getSupportedPredicate() {
        return mSupportedPredicate;
    }

    public static MarathonEventType get(String aName) {

        for (MarathonEventType type : EnumSet.allOf(MarathonEventType.class)) {

            if (type.getName().equals(aName)) {
                return type;
            }

        }

        log.debug("No known handler for event name: " + aName);
        return null;
    }

    private static Predicate<Version> pTrue() {
        return (version) -> true;
    }

    private static Predicate<Version> pFalse() {
        return (version) -> false;
    }

}
