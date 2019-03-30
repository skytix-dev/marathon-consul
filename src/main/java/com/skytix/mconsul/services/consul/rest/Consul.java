package com.skytix.mconsul.services.consul.rest;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

/**
 * Created by marcde on 9/10/2015.
 */
public interface Consul {
    @RequestLine("GET /v1/catalog/service/{serviceName}")
    public List<ServiceNode> getService(@Param("serviceName") String aServiceName);

    @RequestLine("GET /v1/catalog/nodes")
    public List<AgentNode> getNodes();

    @RequestLine("GET /v1/catalog/services")
    public Map<String, List<String>> getServices();

    @RequestLine("GET /v1/agent/services")
    public Map<String, AgentServiceNode> getAgentServices();

    @RequestLine("PUT /v1/agent/service/register")
    public void registerService(ConsulCatalogRegister aCatalogRegister);

    @RequestLine("PUT /v1/agent/service/deregister/{serviceId}")
    @Headers("Content-Type: application/json")
    public void removeServiceFromAgent(@Param("serviceId") String aServiceId);

}
