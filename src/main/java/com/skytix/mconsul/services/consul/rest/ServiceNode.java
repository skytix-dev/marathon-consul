package com.skytix.mconsul.services.consul.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by marcde on 9/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface ServiceNode {
    @JsonProperty("Node")
    public String getNode();
    @JsonProperty("Address")
    public String getAddress();
    @JsonProperty("ServiceID")
    public String getServiceID();
    @JsonProperty("ServiceName")
    public String getServiceName();
    @JsonProperty("ServiceTags")
    public String[] getServiceTags();
    @JsonProperty("ServiceAddress")
    public String getServiceAddress();
    @JsonProperty("ServicePort")
    public int getServicePort();
    @JsonProperty("ServiceMeta")
    public Map<String, String> getServiceMeta();
}
