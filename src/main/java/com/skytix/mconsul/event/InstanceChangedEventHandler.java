package com.skytix.mconsul.event;

import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.marathon.MarathonService;
import com.skytix.mconsul.utils.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

/**
 * Created by xfire on 11/10/2015.
 */
public class InstanceChangedEventHandler extends AbstractEventHandler<InstanceChangedEvent> {
    private static final Logger log = LoggerFactory.getLogger(InstanceChangedEventHandler.class);

    public InstanceChangedEventHandler(MarathonService aMarathonService, ConsulService aConsulService, ErrorHandler aErrorHandler) {
        super(aMarathonService, aConsulService, aErrorHandler);
    }

    @Override
    public void handle(InstanceChangedEvent aEvent, Version aMarathonVersion) {
        /*final String agentId;
        final String eventAgentId = aEvent.getAgentId();
        final String appId = MarathonUtils.parseAppName(aEvent.getRunSpecId());

        if (eventAgentId.startsWith("/")) {
            agentId = eventAgentId.substring(1);

        } else {
            agentId = eventAgentId;
        }

        final String instanceId = aEvent.getInstanceId();

        if (aEvent.isTerminal()) {
            // Everything else is a terminal state pretty much as far as service visibility for a task.
            // Shut this puppy down.
            if (getConsulService().removeInstance(instanceId)) {
                log.info("Application instance terminated: "+ instanceId +"'");
            }

            getTaskLockService().releaseTask(appId);

        } else {
            final ApplicationInstance appInstance = getMarathonService().getInstanceByAppIdAndAgentId(aEvent.getRunSpecId(), agentId);

            if (appInstance != null) {

                switch (aEvent.getCondition().toLowerCase()) {

                    case "created":
                        log.info("Instance is starting up: " + appInstance.getAppName()+":"+appInstance.getHostName()+":" + StringUtils.join(appInstance.getPorts(), ","));
                        getTaskLockService().lockTask(appId);

                        break;

                    case "starting":
                    case "started":
                    case "staging":
                        getTaskLockService().lockTask(appInstance.getAppName());

                        break;

                    case "running":

                        if (!getMarathonService().containsHealthChecks(aEvent.getAppId())) {

                            if (getConsulService().createNode(appInstance, instanceId)) {
                                log.info("Application instance '"+appInstance+"' has been created.");
                            }

                                getTaskLockService().releaseTask(appId);

                        } else {
                            log.info("Application started, awaiting health checks: "+appId+".");
                        }

                        break;

                    case "lost": // We don't want to touch a lost service. It could still be up and running but the agent may have lost contact with the masters.
                        log.warn("Application " + appInstance + " is currently lost.  Leaving service record");

                        break;


                }

            }

        }*/

    }

}
