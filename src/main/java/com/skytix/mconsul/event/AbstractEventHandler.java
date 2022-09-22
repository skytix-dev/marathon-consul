package com.skytix.mconsul.event;

import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.marathon.MarathonService;
import org.springframework.util.ErrorHandler;

/**
 * Created by marcde on 9/10/2015.
 */
public abstract class AbstractEventHandler<T extends MarathonEvent> implements MarathonEventHandler<T> {
    private final MarathonService mMarathonService;
    private final ConsulService mConsulService;
    private final ErrorHandler mErrorHandler;

    protected AbstractEventHandler(MarathonService aMarathonService, ConsulService aConsulService, ErrorHandler aErrorHandler) {
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

    protected ErrorHandler getErrorHandler() {
        return mErrorHandler;
    }
}
