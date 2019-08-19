package com.skytix.mconsul.event;

import com.skytix.mconsul.ApplicationErrorHandler;
import com.skytix.mconsul.models.ApplicationInstance;
import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.consul.ConsulServiceException;
import com.skytix.mconsul.services.marathon.MarathonService;
import com.skytix.mconsul.utils.Version;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by marcde on 9/10/2015.
 */
public class InstanceHealthChangedEventHandler extends AbstractEventHandler<InstanceHealthChangedEvent> {
    private static final Logger log = LoggerFactory.getLogger(InstanceHealthChangedEventHandler.class);

    public InstanceHealthChangedEventHandler(MarathonService aMarathonService, ConsulService aConsulService, ApplicationErrorHandler aErrorHandler) {
        super(aMarathonService, aConsulService, aErrorHandler);
    }

    @Override
    public void handle(InstanceHealthChangedEvent aEvent, Version aMarathonVersion) {

        try {
            final ApplicationInstance appInstance = getMarathonService().getInstanceByInstanceId(aEvent.getInstanceId());

            if (appInstance != null) {

                if (aEvent.isHealthy()) {

                    if (getConsulService().createNode(appInstance)) {
                        log.info("Instance is now alive: " + appInstance.getAppName()+":"+appInstance.getHostName()+":" + StringUtils.join(appInstance.getPorts(), ','));
                    }

                } else {

                    if (getConsulService().removeInstance(appInstance.getId())) {
                        log.info("Instance is no longer alive: " + appInstance.getAppName()+":"+appInstance.getHostName()+":" + StringUtils.join(appInstance.getPorts(), ','));
                    }

                }

            }

        } catch (ConsulServiceException e) {
            getErrorHandler().handle(e);
        }

    }

}
