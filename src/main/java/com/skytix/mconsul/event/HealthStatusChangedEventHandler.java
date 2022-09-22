package com.skytix.mconsul.event;

import com.skytix.mconsul.models.ApplicationInstance;
import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.consul.ConsulServiceException;
import com.skytix.mconsul.services.marathon.MarathonService;
import com.skytix.mconsul.utils.Version;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

/**
 * Created by marcde on 9/10/2015.
 */
public class HealthStatusChangedEventHandler extends AbstractEventHandler<HealthStatusChangedEvent> {
    private static final Logger log = LoggerFactory.getLogger(HealthStatusChangedEventHandler.class);

    public HealthStatusChangedEventHandler(MarathonService aMarathonService, ConsulService aConsulService, ErrorHandler aErrorHandler) {
        super(aMarathonService, aConsulService, aErrorHandler);
    }

    @Override
    public void handle(HealthStatusChangedEvent aEvent, Version aMarathonVersion) {

        try {
            final String instanceId = aEvent.getInstanceId();
            final ApplicationInstance appInstance = getMarathonService().getInstanceByInstanceId(instanceId);

            if (appInstance != null) {

                if (aEvent.getAlive()) {
                    log.info("Instance is now alive: " + appInstance.getAppName()+":"+appInstance.getHostName()+":" + StringUtils.join(appInstance.getPorts(), ','));
                    getConsulService().createNode(appInstance);

                } else {
                    log.info("Instance is no longer alive: " + appInstance.getAppName()+":"+appInstance.getHostName()+":" + StringUtils.join(appInstance.getPorts(), ','));
                    getConsulService().removeInstance(appInstance.getId());
                }

            }

        } catch (ConsulServiceException e) {
            getErrorHandler().handleError(e);
        }

    }

}
