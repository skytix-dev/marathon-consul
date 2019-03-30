package com.skytix.mconsul.services.marathon.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skytix.mconsul.event.TaskStatus;

/**
 * Created by marcde on 9/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface Task {
    public String getId();
    public String getAppId();
    public String getHost();
    public int[] getPorts();
    public String getSlaveId();
    public TaskStatus getState();
    public HealthCheckResult[] getHealthCheckResults();
}
