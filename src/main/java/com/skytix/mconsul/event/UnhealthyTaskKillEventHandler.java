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
 * Created by xfire on 11/10/2015.
 */
public class UnhealthyTaskKillEventHandler extends AbstractEventHandler<UnhealthyTaskKillEvent> {
    private static final Logger log = LoggerFactory.getLogger(UnhealthyTaskKillEventHandler.class);

    public UnhealthyTaskKillEventHandler(MarathonService aMarathonService, ConsulService aConsulService, ApplicationErrorHandler aErrorHandler) {
        super(aMarathonService, aConsulService, aErrorHandler);
    }

    @Override
    public void handle(UnhealthyTaskKillEvent aEvent, Version aMarathonVersion) {

        try {
            final ApplicationInstance failedInstance = getMarathonService().getInstanceById(aEvent.getTaskId());

            if (failedInstance != null) {
                log.info("Instance was unhealthy and has been killed: " + failedInstance.getAppName()+":"+failedInstance.getHostName()+":" + StringUtils.join(failedInstance.getPorts(), ','));
                getConsulService().removeInstance(failedInstance.getId());
            }

        } catch (ConsulServiceException e) {
            getErrorHandler().handle(e);
        }

    }

}
