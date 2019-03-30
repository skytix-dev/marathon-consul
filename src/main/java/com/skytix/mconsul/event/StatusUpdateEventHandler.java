package com.skytix.mconsul.event;

import com.skytix.mconsul.models.ApplicationInstance;
import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.marathon.MarathonService;
import com.skytix.mconsul.services.marathon.MarathonUtils;
import com.skytix.mconsul.utils.Version;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by marcde on 9/10/2015.
 */
public class StatusUpdateEventHandler extends AbstractEventHandler<StatusUpdateEvent> {
    private static final Logger log = LoggerFactory.getLogger(StatusUpdateEventHandler.class);

    public StatusUpdateEventHandler(MarathonService aMarathonService, ConsulService aConsulService) {
        super(aMarathonService, aConsulService);
    }

    @Override
    public void handle(StatusUpdateEvent aEvent, Version aMarathonVersion) {
        final String taskStatus = aEvent.getTaskStatus();

        if (!StringUtils.isEmpty(taskStatus)) {
            final boolean containsHealthChecks = getMarathonService().containsHealthChecks(aEvent.getAppId());
            final TaskStatus status = TaskStatus.valueOf(taskStatus);
            final ApplicationInstance appInstance = new ApplicationInstance(aEvent.getTaskId(), aEvent.getAppId(), MarathonUtils.parseAppName(aEvent.getAppId()), aEvent.getHost(), aEvent.getPorts(), status);

            if (status.isTerminal()) {
                // Shut this puppy down.
                if (getConsulService().removeInstance(appInstance.getId())) {
                    log.info("Application terminated: "+appInstance+"'.");
                }

            } else {

                if (status == TaskStatus.TASK_RUNNING) {

                    if (!containsHealthChecks) {
                        // We need to check to see if the task has any healthchecks.  If so, we will let the health check failure handler take care of it.
                        // Same goes for the tasks becomming healthy again.
                        if (getConsulService().createNode(appInstance)) {
                            log.info("Application instance '" + appInstance + "' has been created.");
                        }

                    }

                } else {
                    log.trace("Status: " + status + " is neither running nor terminal");
                }

            }

        }

    }

}
