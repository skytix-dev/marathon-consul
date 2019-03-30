package com.skytix.mconsul;

import com.skytix.mconsul.event.MarathonEventType;
import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.marathon.MarathonService;
import com.skytix.mconsul.utils.Version;

/**
 * Created by marcde on 13/10/2015.
 */
public class MarathonEventHandler {
    private final MarathonService mMarathonService;
    private final ConsulService mConsulService;
    private final Object mWriteLock;
    private final Version mMarathonVersion;

    public MarathonEventHandler(MarathonService aMarathonService, ConsulService aConsulService, Object aWriteLock, Version aMarathonVersion) {
        mMarathonService = aMarathonService;
        mConsulService = aConsulService;
        mWriteLock = aWriteLock;
        mMarathonVersion = aMarathonVersion;
    }

    public void onEvent(MarathonSSEEvent event) throws Exception {
        final MarathonEventType marathonEventType = event.getMarathonEventType();

        if (event.getMarathonEvent() != null && marathonEventType != null && marathonEventType.getSupportedPredicate().test(mMarathonVersion)) {

            synchronized (mWriteLock) {

                marathonEventType.getEventHandlerBuilder()
                        .build(mMarathonService, mConsulService)
                        .handle(event.getMarathonEvent(), mMarathonVersion);

            }

        }

    }

}
