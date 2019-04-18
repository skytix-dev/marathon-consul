package com.skytix.mconsul;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skytix.mconsul.event.MarathonEvent;
import com.skytix.mconsul.event.MarathonEventType;
import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.marathon.MarathonService;
import com.skytix.mconsul.services.zookeeper.ZooKeeperService;
import com.skytix.mconsul.utils.Version;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Created by marcde on 7/10/2015.
 */
@Controller
public class RegistrationRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(RegistrationRunner.class);
    private static final Object mWriteLock = new Object();

    @Autowired
    private MarathonService mMarathonService;
    @Autowired
    private ConsulService mConsulService;
    @Autowired
    private ObjectMapper mObjectMapper;
    @Autowired
    private ZooKeeperService mZooKeeperService;

    @Value("${inactivityExpireTime:60}")
    private int mInactivityExpireTime;

    @Value("${sseReconnectInterval:10}")
    private int mSseReconnectInterval;

    private boolean mRunning = true;
    private boolean mConnected = true;
    private MarathonEventHandler mEventHandler;
    private Version mMarathonVersion;
    private LeaderLatch mLeaderLatch;

    @PostConstruct
    public void init() {
        final String marathonVersion = mMarathonService.getMarathonVersion().replaceAll("\\.\\d+$", "");
        mMarathonVersion = Version.of(marathonVersion);

        log.info("Marathon version: " + mMarathonVersion.get());

        mEventHandler = new MarathonEventHandler(mMarathonService, mConsulService, mWriteLock, mMarathonVersion);
    }

    @Override
    public void run(String... args) throws Exception {
        mLeaderLatch = mZooKeeperService.getLeaderLatch();
        mRunning = true;

        while (mRunning) {

            try {

                while (!mLeaderLatch.await(5, TimeUnit.SECONDS) && mRunning) {
                    log.trace("Instance waiting for leadership...");
                }

                while (mLeaderLatch.hasLeadership()) {
                    init();
                    mConnected = true;

                    final String leader = mMarathonService.getLeader();

                    log.info("Subscribing to event queue on host '" + leader + "'");

                    final ParameterizedTypeReference<ServerSentEvent<String>> typeRef = new ParameterizedTypeReference<>() { /* nothing_here */ };
                    final WebClient webClient = WebClient.create(leader);

                    final Flux<ServerSentEvent<String>> stream = webClient
                            .get()
                            .uri("/v2/events")
                            .accept(MediaType.TEXT_EVENT_STREAM)
                            .retrieve()
                            .bodyToFlux(typeRef);

                    final Disposable disposable = stream
                            .doOnError(this::stop)
                            .doOnTerminate(() -> {
                                log.info("Connection to leader " + leader + " has closed");
                                stop();
                            })
                            .subscribe(
                                    this::handleEvent,
                                    this::stop
                            );

                    while (mConnected && mLeaderLatch.hasLeadership()) {
                        Thread.sleep(500);
                    }

                    disposable.dispose();
                }

            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                mRunning = false;
            }

        }

        stop();
    }

    private void handleEvent(ServerSentEvent<String> aEvent) {

        try {
            final String data = aEvent.data();
            final JsonParser parser = mObjectMapper.getFactory().createParser(data);
            final String eventName = aEvent.event().trim(); // Not sure why there's a whitespace at the front of the event name. Possible bug.

            final MarathonEventType eventType = MarathonEventType.get(eventName);

            if (eventType != null && eventType.getSupportedPredicate().test(mMarathonVersion)) {
                log.info("Event message: " + data);

                final MarathonSSEEvent event = new MarathonSSEEvent();
                final MarathonEvent marathonEvent = mObjectMapper.readValue(parser, eventType.getEventClass());

                event.setMarathonEvent(marathonEvent);
                event.setMarathonEventType(eventType);

                mEventHandler.onEvent(event);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private void stop(Throwable e) {
        log.error(e.getMessage(), e);
        stop();
    }

    private void stop() {
        mConnected = false;
    }

}
