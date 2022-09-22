package com.skytix.mconsul.services.marathon.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Created by marcde on 9/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface MarathonApplication {
    public String getId();
    public int getTasksStaged();
    public int getTasksRunning();
    public int getTasksHealthy();
    public int getTasksUnhealthy();
    public Task[] getTasks();
    public HealthCheck[] getHealthChecks();
    public Map<String, String> getLabels();
}
