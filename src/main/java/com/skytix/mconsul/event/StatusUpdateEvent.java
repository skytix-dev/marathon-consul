package com.skytix.mconsul.event;

/**
 * Created by xfire on 8/10/2015.
 */
public interface StatusUpdateEvent extends BaseMarathonEvent {

    public String getSlaveId();
    public String getTaskId();
    public String getTaskStatus();
    public String getHost();
    public int[] getPorts();
    public String getVersion();
}
