package com.skytix.mconsul.services.marathon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.skytix.mconsul.RegistrationRunner;
import com.skytix.mconsul.models.ApplicationInstance;
import com.skytix.mconsul.models.ServiceTree;
import com.skytix.mconsul.services.marathon.rest.*;
import com.skytix.mconsul.services.zookeeper.ZooKeeperException;
import com.skytix.mconsul.services.zookeeper.ZooKeeperService;
import feign.Feign;
import feign.FeignException;
import feign.RetryableException;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by marcde on 7/10/2015.
 */
@Component
public class MarathonService {
    private static final Logger log = LoggerFactory.getLogger(RegistrationRunner.class);
    private static final String MARATHON_LEADER_PATH = "/leader-curator";

    @Autowired
    private ZooKeeperService mZooKeeperService;
    @Autowired
    private ObjectMapper mObjectMapper;
    @Value("${useLocalhost")
    private String mUseLocalhost = "false";

    private final LoadingCache<String, MarathonApi> mMarathonCache = CacheBuilder.newBuilder().build(new CacheLoader<String, MarathonApi>() {

        @Override
        public MarathonApi load(String key) throws Exception {
            return Feign.builder()
                    .decoder(new JacksonDecoder(mObjectMapper))
                    .encoder(new JacksonEncoder(mObjectMapper))
                    .target(MarathonApi.class, key);
        }
    });

    private String getMarathonLeader() throws ZooKeeperException {

        if (mZooKeeperService.nodeExists(MARATHON_LEADER_PATH)) {
            final List<String> leaders = mZooKeeperService.getChildren(MARATHON_LEADER_PATH);

            if (leaders.size() > 0) {
                final Pattern p = Pattern.compile(".*?-latch-(\\d+)$");

                leaders.sort((o1, o2) -> {
                    final Matcher m1 = p.matcher(o1);
                    final Matcher m2 = p.matcher(o2);

                    if (m1.matches() && m2.matches()) {
                        final Integer v1 = Integer.parseInt(m1.group(1));
                        final Integer v2 = Integer.parseInt(m2.group(1));

                        return v1.compareTo(v2);

                    } else {
                        throw new RuntimeException("Unable to find the leader id from: ('"+o1+"', '" + o2 + "') ");
                    }
                });

                // The very first info node we get should be of the current leader.  The hostname:port is in the node
                final String leader = mZooKeeperService.getNode(MARATHON_LEADER_PATH + "/" + leaders.get(0));
                final String uri = new StringBuilder(isSSL(leader) ? "https" : "http").append("://").append(leader).toString();
                return uri;
            }

            throw new ZooKeeperException("Unable to find Marathon leaders.  There are no master nodes registered in ZooKeeper");

        } else {
            throw new ZooKeeperException("Unable to find Marathon leaders.  " + MARATHON_LEADER_PATH + " does not exist in ZooKeeper");
        }

    }

    private MarathonApplication getApplication(String aAppName) {

        try {
            return getRestInterface().getApplication(aAppName).getApp();

        } catch (FeignException aE) {
            return null;
        }

    }

    public ServiceTree getApplications() {
        final List<MarathonApplication> apps = getRestInterface().getApplications().getApps();
        return new ServiceTree(apps.toArray(new MarathonApplication[apps.size()]));
    }

    private MarathonApi getRestInterface() {
        return getRestInterface(getLeader());
    }

    private MarathonApi getRestInterface(String aHost) {

        try {
            return mMarathonCache.get(aHost);

        } catch (ExecutionException aE) {
            throw new RuntimeException(aE);
        }

    }

    private boolean isSSL(String aHost) {

        try {
            mMarathonCache.get("https://" + aHost).ping();
            return true;

        } catch (RetryableException e) {

            if (e.getCause() instanceof SSLException) {
                return false;

            } else {
                throw e;
            }

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    public ApplicationInstance getInstanceById(String aTaskId) {

        for (Task task : getRestInterface().getTasks().getTasks()) {

            if (task.getId().equals(aTaskId)) {
                return MarathonUtils.buildAppInstance(task);
            }

        }

        return null;
    }

    public Task getTaskById(String aTaskId) {
        for (Task task : getRestInterface().getTasks().getTasks()) {

            if (task.getId().equals(aTaskId)) {
                return task;
            }

        }

        return null;
    }

    public Task getTaskByInstanceId(String aInstanceId) {

        for (Task task : getRestInterface().getTasks().getTasks()) {
            final HealthCheckResult[] healthCheckResults = task.getHealthCheckResults();

            if (healthCheckResults != null) {

                for (HealthCheckResult healthCheckResult : healthCheckResults) {
                    final String instanceId = MarathonUtils.extractInstanceId(healthCheckResult.getInstanceId());

                    if (StringUtils.isNotBlank(instanceId) && instanceId.equals(aInstanceId)) {
                        return task;
                    }

                }

            }

        }

        return null;
    }

    public ApplicationInstance getInstanceByInstanceId(String aInstanceId) {
        final Task task = getTaskByInstanceId(aInstanceId);

        if (task != null) {
            return MarathonUtils.buildAppInstance(task);
        }

        return null;
    }

    public ApplicationInstance getInstanceByAppIdAndAgentId(String aAppId, String aAgentId) {

        for (Task task : getRestInterface().getAppTasks(aAppId).getTasks()) {

            if (task.getSlaveId().equals(aAgentId)) {
                return MarathonUtils.buildAppInstance(task);
            }

        }

        return null;
    }

    public boolean containsHealthChecks(String aAppName) {
        final MarathonApplication app = getApplication(aAppName);

        if (app != null) {
            final HealthCheck[] healthChecks = app.getHealthChecks();
            return healthChecks != null && healthChecks.length > 0;

        } else {
            return false;
        }


    }

    public String getLeader() {

        synchronized (this) {

            try {

                if (Boolean.parseBoolean(mUseLocalhost)) {
                    return "http://127.0.0.1:8080";

                } else {
                    return getMarathonLeader();
                }

            } catch (ZooKeeperException aE) {
                log.error("Unable to discover Marathon masters from zookeeper: "+aE.getMessage(), aE);
                return null;
            }

        }

    }

    public String getMarathonVersion() {
        return getRestInterface().getServerInfo().getVersion();
    }

    public String getMesosLeader() {
        return getRestInterface().getServerInfo().getMarathonConfig().getMesosLeaderUrl();
    }

}
