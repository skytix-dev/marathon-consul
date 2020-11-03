package com.skytix.mconsul.services.consul;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.skytix.mconsul.models.ApplicationInstance;
import com.skytix.mconsul.models.ServiceTree;
import com.skytix.mconsul.services.consul.rest.*;
import feign.Feign;
import feign.RetryableException;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by marcde on 7/10/2015.
 */
@Component
public class ConsulService {
    private static final Logger log = LoggerFactory.getLogger(ConsulService.class);
    private final List<String> EXCLUDED_SERVICE_NAMES = new ArrayList<>();

    private final LoadingCache<String, Consul> mConsulCache = CacheBuilder.newBuilder().build(new CacheLoader<>() {

        @Override
        public Consul load(String key) throws Exception {
            return Feign.builder()
                    .decoder(new JacksonDecoder(mObjectMapper))
                    .encoder(new JacksonEncoder(mObjectMapper))
                    .target(Consul.class, key);
        }
    });

    @Value("${consulAgent:consul.service.consul:8500}")
    private String mConsulHost;

    @Value("${excludedServiceNames}")
    private String mExcludedServiceNames;

    @Autowired
    private ObjectMapper mObjectMapper;

    @PostConstruct
    public void init() {
        EXCLUDED_SERVICE_NAMES.add("consul");

        if (StringUtils.isNotBlank(mExcludedServiceNames)) {
            final String[] serviceNames = mExcludedServiceNames.split(",");

            if (serviceNames.length >= 1) {
                Collections.addAll(EXCLUDED_SERVICE_NAMES, serviceNames);
            }

        }

    }

    /**
     * Returns all services registered in Consul except for the Consul service itself.
     * @return List of all Service nodes for all Apps
     */
    public ServiceTree getCurrentServices() {
        try {
            final Map<String, List<ServiceNode>> nodes = new HashMap<>();
            final Map<String, List<String>> consulServices = getRestInterface().getServices();

            for (String serviceId : consulServices.keySet()) {

                if (!EXCLUDED_SERVICE_NAMES.contains(serviceId)) {
                    final String serviceName;

                    if (serviceId.matches("^.*?-port[0-9]+$")) {
                        serviceName = serviceId.substring(0, serviceId.lastIndexOf("-"));

                    } else {
                        serviceName = serviceId;
                    }

                    if (!nodes.containsKey(serviceName)) {
                        nodes.put(serviceName, new ArrayList<>());
                    }

                    nodes.get(serviceName).addAll(getServiceNodes(serviceId));
                }

            }

            return new ServiceTree(nodes);

        } catch (RetryableException e) {
            final Throwable cause = e.getCause();

            if (cause instanceof SocketTimeoutException | cause instanceof ConnectException) {
                throw new ConsulServiceException(e);

            } else {
                throw e;
            }

        }

    }

    public List<ServiceNode> getServiceNodes(String aServiceName) {

        try {
            return getRestInterface().getService(aServiceName);

        } catch (RetryableException e) {
            final Throwable cause = e.getCause();

            if (cause instanceof SocketTimeoutException | cause instanceof ConnectException) {
                throw new ConsulServiceException(e);

            } else {
                throw e;
            }

        }

    }

    public boolean removeInstance(String aAppInstanceId) {

        try {
            boolean removed = false;

            // When we remove an application, we may not always be running on the same node as when it was registered so we need to go through all the nodes we can find
            // and remove the service from there otherwise there will be service registration conflicts.
            for (AgentNode node : getRestInterface().getNodes()) {
                final Map<String, AgentServiceNode> agentServices = getRestInterface(node).getAgentServices();

                for (AgentServiceNode agentServiceNode : agentServices.values()) {
                    final String serviceId = agentServiceNode.getId();
                    final String serviceNodeId = ConsulUtils.getAppNamePart(serviceId);

                    if (serviceNodeId.equals(aAppInstanceId)) {
                        getRestInterface(node).removeServiceFromAgent(serviceId);
                        removed = true;
                    }

                }

            }

            return removed;

        } catch (RetryableException e) {
            final Throwable cause = e.getCause();

            if (cause instanceof SocketTimeoutException | cause instanceof ConnectException) {
                throw new ConsulServiceException(e);

            } else {
                throw e;
            }

        }

    }

    public boolean createNode(ApplicationInstance aApplicationInstance) {
        return createNode(aApplicationInstance, aApplicationInstance.getId());
    }

    public boolean createNode(ApplicationInstance aApplicationInstance, String aServiceId) {

        try {
            final String appName = aApplicationInstance.getAppName();
            final int[] ports = aApplicationInstance.getPorts();
            final int numPorts = ports.length;

            boolean created = false;

            if (numPorts > 1) {

                for (int i = 0; i < numPorts; i++) {
                    final String serviceName = appName+"-port"+i;
                    final String serviceId = aServiceId+"-port"+i;

                    if (!doesInstanceExist(serviceName, aApplicationInstance)) {
                        registerService(serviceId, serviceName, aApplicationInstance.getHostName(), ports[i]);
                        created = true;
                    }
                }

            } else {

                if (!doesInstanceExist(appName, aApplicationInstance)) {

                    if (ports.length > 0) {
                        registerService(aServiceId, appName, aApplicationInstance.getHostName(), ports[0]);
                        created = true;

                    } else {
                        log.info("Application " + aApplicationInstance + " has no ports defined.  Ignoring.");
                    }

                }

            }


            return created;

        } catch (RetryableException e) {
            final Throwable cause = e.getCause();

            if (cause instanceof SocketTimeoutException | cause instanceof ConnectException) {
                throw new ConsulServiceException(e);

            } else {
                throw e;
            }

        }

    }

    private boolean doesInstanceExist(String aServiceName, ApplicationInstance aAppInstance) {
        final List<ServiceNode> serviceNodes = getServiceNodes(aServiceName);

        for (ServiceNode node : serviceNodes) {

            if (node.getServiceAddress().equals(aAppInstance.getHostName()) && ArrayUtils.contains(aAppInstance.getPorts(), node.getServicePort())) {
                return true;
            }

        }

        return false;
    }

    private void registerService(String aServiceId, String aServiceName, String aAddress, int aPort) {

        getRestInterface().registerService(
            new ConsulCatalogRegister(
                aServiceId,
                aServiceName,
                aAddress,
                aPort
            )
        );

    }

    private Consul getRestInterface() {

        try {
            return mConsulCache.get("http://"+mConsulHost);

        } catch (ExecutionException aE) {
            throw new RuntimeException(aE);
        }

    }

    private Consul getRestInterface(AgentNode aNode) {

        try {
            return mConsulCache.get("http://"+aNode.getAddress()+":8500");

        } catch (ExecutionException aE) {
            throw new RuntimeException(aE);
        }

    }

}
