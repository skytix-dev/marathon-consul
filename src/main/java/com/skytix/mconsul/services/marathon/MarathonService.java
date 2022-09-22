package com.skytix.mconsul.services.marathon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.skytix.mconsul.RegistrationRunner;
import com.skytix.mconsul.models.ApplicationInstance;
import com.skytix.mconsul.models.ServiceTree;
import com.skytix.mconsul.services.marathon.rest.*;
import feign.Client;
import feign.Feign;
import feign.FeignException;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Created by marcde on 7/10/2015.
 */
@Component
public class MarathonService {
    private static final Logger log = LoggerFactory.getLogger(RegistrationRunner.class);
    private static final String MARATHON_LEADER_PATH = "/leader-curator";

    @Autowired
    private ObjectMapper mObjectMapper;
    @Value("${marathonHost}")
    private String marathonHost;
    @Value("${useLocalhost:false")
    private String mUseLocalhost = "false";
    @Autowired
    private HttpClient mHttpClient;
    @Autowired
    private SSLContext mSslContext;

    private final LoadingCache<String, MarathonApi> mMarathonCache = CacheBuilder.newBuilder().build(new CacheLoader<>() {

        @Override
        public MarathonApi load(String key) throws Exception {
            final Feign.Builder builder = Feign.builder()
                    .decoder(new JacksonDecoder(mObjectMapper))
                    .encoder(new JacksonEncoder(mObjectMapper));

            if (mSslContext != null) {
                builder.client(new Client.Default(mSslContext.getSocketFactory(), (s, sslSession) -> true));
            }

            return builder.target(MarathonApi.class, key);
        }

    });

    private String getMarathonLeader() throws IOException {

        try {
            final URI uri = new URI(String.format("%s", marathonHost));

            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            final HttpResponse<String> response = mHttpClient.send(request, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()) {

                case 302:
                    final Optional<String> location = response.headers().firstValue("x-marathon-leader");

                    if (location.isPresent()) {
                        return location.get();

                    } else {
                        throw new IOException("Unable to find leader in missing x-marathon-leader header");
                    }

                case 503:
                    throw new IOException("An elected Marathon leader node cannot be found");

                default:
                    throw new IOException("Unable to determine the current leader");
            }

        } catch (URISyntaxException | InterruptedException aE) {
            throw new IOException(aE);
        }

    }

    public MarathonApplication getApplication(String aAppName) {

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

    public ApplicationInstance getInstanceById(String aTaskId) {

        for (Task task : getRestInterface().getTasks().getTasks()) {

            if (task.getId().equals(aTaskId)) {
                return MarathonUtils.buildAppInstance(getApplication(task.getAppId()), task);
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
            final MarathonApplication application = getApplication(task.getAppId());
            return MarathonUtils.buildAppInstance(application, task);
        }

        return null;
    }

    public ApplicationInstance getInstanceByAppIdAndAgentId(String aAppId, String aAgentId) {
        final MarathonApplication application = getApplication(aAppId);

        for (Task task : application.getTasks()) {

            if (task.getSlaveId().equals(aAgentId)) {
                return MarathonUtils.buildAppInstance(application, task);
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

            } catch (IOException aE) {
                log.error("Unable to discover Marathon masters: "+aE.getMessage(), aE);
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
