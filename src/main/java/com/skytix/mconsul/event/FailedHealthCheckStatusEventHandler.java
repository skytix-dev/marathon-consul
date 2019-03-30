package com.skytix.mconsul.event;

import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.marathon.MarathonService;
import com.skytix.mconsul.utils.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfire on 11/10/2015.
 */
public class FailedHealthCheckStatusEventHandler extends AbstractEventHandler<FailedHealthCheckEvent> {
    private static final Logger log = LoggerFactory.getLogger(FailedHealthCheckStatusEventHandler.class);

    public FailedHealthCheckStatusEventHandler(MarathonService aMarathonService, ConsulService aConsulService) {
        super(aMarathonService, aConsulService);
    }

    @Override
    public void handle(FailedHealthCheckEvent aEvent, Version aMarathonVersion) {
        /*final String taskId = aEvent.getTaskId();
        final ApplicationInstance failedInstance = getMarathonService().getInstanceByInstanceId(taskId);

        if (failedInstance != null) {
            log.info("Instance has failed health checks: " + failedInstance.getAppName()+":"+failedInstance.getHostName()+":" + StringUtils.join(failedInstance.getPorts(), ","));
            getConsulService().removeInstance(failedInstance.getId());
        }*/

    }

}
