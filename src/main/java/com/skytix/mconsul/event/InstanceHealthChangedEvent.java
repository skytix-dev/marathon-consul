package com.skytix.mconsul.event;

/**
 * Created by marc on 31/03/2017.
 */
public interface InstanceHealthChangedEvent extends BaseMarathonEvent {
    public String getInstanceId();
    public String getRunSpecId();
    public boolean isHealthy();
}
