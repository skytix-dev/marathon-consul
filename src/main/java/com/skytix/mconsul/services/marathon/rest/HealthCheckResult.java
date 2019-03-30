package com.skytix.mconsul.services.marathon.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTime;

/**
 * Created by xfire on 11/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface HealthCheckResult {
    public boolean isAlive();
    public int getConsecutiveFailures();
    public DateTime getFirstSuccess();
    public DateTime getLastFailure();
    public DateTime getLastSuccess();
    public String getTaskId();
    public String getInstanceId();
}
