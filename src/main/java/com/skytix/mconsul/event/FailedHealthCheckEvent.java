package com.skytix.mconsul.event;

import com.skytix.mconsul.services.marathon.rest.HealthCheck;

/**
 * Created by xfire on 11/10/2015.
 */
public interface FailedHealthCheckEvent extends BaseMarathonEvent {
    public String getTaskId();
    public String getInstanceId();
    public HealthCheck getHealthCheck();
}
