package com.skytix.mconsul.event;

import com.skytix.mconsul.ApplicationErrorHandler;
import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.marathon.MarathonService;

/**
 * Created by marcde on 9/10/2015.
 */
public abstract class AbstractEventHandler<T extends MarathonEvent> implements MarathonEventHandler<T> {
    private final MarathonService mMarathonService;
    private final ConsulService mConsulService;
    private final ApplicationErrorHandler mErrorHandler;

    protected AbstractEventHandler(MarathonService aMarathonService, ConsulService aConsulService, ApplicationErrorHandler aErrorHandler) {
        mMarathonService = aMarathonService;
        mConsulService = aConsulService;
        mErrorHandler = aErrorHandler;
    }

    protected MarathonService getMarathonService() {
        return mMarathonService;
    }

    protected ConsulService getConsulService() {
        return mConsulService;
    }

    protected ApplicationErrorHandler getErrorHandler() {
        return mErrorHandler;
    }
}
