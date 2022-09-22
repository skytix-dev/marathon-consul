package com.skytix.mconsul.event;

import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.marathon.MarathonService;
import org.springframework.util.ErrorHandler;

/**
 * Created by marcde on 9/10/2015.
 */
public interface EventHandlerBuilder<E extends MarathonEvent, T extends MarathonEventHandler<E>> {
    public T build(MarathonService aMarathonService, ConsulService aConsulService, ErrorHandler aErrorHandler);
}
