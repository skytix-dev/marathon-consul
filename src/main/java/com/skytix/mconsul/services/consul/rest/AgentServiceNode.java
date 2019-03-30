package com.skytix.mconsul.services.consul.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by marcde on 9/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface AgentServiceNode {
    @JsonProperty("ID")
    public String getId();
    @JsonProperty("Service")
    public String getService();
    @JsonProperty("Tags")
    public String[] getTags();
    @JsonProperty("Address")
    public String getAddress();
    @JsonProperty("Port")
    public int getPort();
}
