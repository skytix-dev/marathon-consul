package com.skytix.mconsul.event;

import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.marathon.MarathonService;

/**
 * Created by marcde on 9/10/2015.
 */
public abstract class AbstractEventHandler<T extends MarathonEvent> implements MarathonEventHandler<T> {
    private final MarathonService mMarathonService;
    private final ConsulService mConsulService;

    protected AbstractEventHandler(MarathonService aMarathonService, ConsulService aConsulService) {
        mMarathonService = aMarathonService;
        mConsulService = aConsulService;
    }

    protected MarathonService getMarathonService() {
        return mMarathonService;
    }

    protected ConsulService getConsulService() {
        return mConsulService;
    }

}
