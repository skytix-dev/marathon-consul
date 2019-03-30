package com.skytix.mconsul.services.consul.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by xfire on 10/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface AgentNode {
    @JsonProperty("Node")
    public String getNode();
    @JsonProperty("Address")
    public String getAddress();
}
