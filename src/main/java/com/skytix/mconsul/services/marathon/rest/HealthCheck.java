package com.skytix.mconsul.services.marathon.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by xfire on 11/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface HealthCheck {
    public String getProtocol();
    public String getPath();
    public int getPortIndex();
    public int getGracePeriodSeconds();
    public int getIntervalSeconds();
    public int getTimeoutSeconds();
    public int getMaxConsecutiveFailures();
    public Boolean getIgnoreHttp1xx();
}
