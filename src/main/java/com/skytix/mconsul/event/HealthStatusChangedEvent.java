package com.skytix.mconsul.event;

/**
 * Created by marcde on 9/10/2015.
 */
public interface HealthStatusChangedEvent extends BaseMarathonEvent {
    public String getInstanceId();
    public Boolean getAlive();
}
