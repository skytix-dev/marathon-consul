package com.skytix.mconsul.services.marathon.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by marc on 13/11/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface MarathonConfig {
    @JsonProperty("mesos_leader_ui_url")
    public String getMesosLeaderUrl();
}
