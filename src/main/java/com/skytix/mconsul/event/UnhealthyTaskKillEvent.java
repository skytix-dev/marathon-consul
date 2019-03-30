package com.skytix.mconsul.event;

/**
 * Created by xfire on 11/10/2015.
 */
public interface UnhealthyTaskKillEvent extends BaseMarathonEvent {
    public String getAppId();
    public String getTaskId();
    public String getInstanceId();
    public String getReason();
}
