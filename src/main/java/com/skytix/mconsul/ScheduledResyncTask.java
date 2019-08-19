package com.skytix.mconsul;

import com.skytix.mconsul.event.TaskStatus;
import com.skytix.mconsul.models.Application;
import com.skytix.mconsul.models.ApplicationInstance;
import com.skytix.mconsul.services.consul.ConsulService;
import com.skytix.mconsul.services.consul.ConsulServiceException;
import com.skytix.mconsul.services.marathon.MarathonService;
import com.skytix.mconsul.services.marathon.rest.HealthCheckResult;
import com.skytix.mconsul.services.marathon.rest.Task;
import com.skytix.mconsul.services.mesos.MesosService;
import com.skytix.mconsul.services.zookeeper.ZooKeeperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledResyncTask {
    private static final Logger log = LoggerFactory.getLogger(ScheduledResyncTask.class);

    @Autowired
    private MarathonService mMarathonService;
    @Autowired
    private ConsulService mConsulService;
    @Autowired
    private MesosService mMesosService;
    @Autowired
    private ZooKeeperService mZooKeeperService;
    @Autowired
    private ApplicationErrorHandler mErrorHandler;

    @Scheduled(fixedRateString = "${resyncInterval:60000}")
    public void syncServices() {

        try {

            if (mZooKeeperService.getLeaderLatch().hasLeadership()) {
                log.info("Performing periodic idle re-sync of services");

                final List<ApplicationInstance> marathonApps = getAppInstances(mMarathonService.getApplications().getApplications());
                final List<ApplicationInstance> consulApps = getAppInstances(mConsulService.getCurrentServices().getApplications());

                deleteOldServices(marathonApps, consulApps);
                createNewServices(marathonApps, consulApps);
            }

        } catch (ConsulServiceException e) {
            log.error(e.getMessage(), e);
            mErrorHandler.handle(e);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private List<ApplicationInstance> getAppInstances(List<Application> aApplications) {
        final List<ApplicationInstance> instances = new ArrayList<>();

        for (Application app : aApplications) {
            instances.addAll(app.getInstances());
        }

        return instances;
    }

    private void deleteOldServices(List<ApplicationInstance> aMarathonInstances, List<ApplicationInstance> aConsulInstances) {

        for (ApplicationInstance consulInstance : aConsulInstances) {

            if (!aMarathonInstances.contains(consulInstance)) {
                // Lost tasks don't appear in the instance list in Marathon so we want to check for them in Mesos.
                final TaskStatus taskState = mMesosService.getTaskState(consulInstance.getId());

                if (taskState == null || taskState.isTerminal()){
                    log.info("Deleting old service: " + consulInstance);
                    mConsulService.removeInstance(consulInstance.getId());
                }

            }

        }

    }

    private void createNewServices(List<ApplicationInstance> aMarathonInstances, List<ApplicationInstance> aConsulInstances) {

        for (ApplicationInstance marathonInstance : aMarathonInstances) {

            if (!aConsulInstances.contains(marathonInstance)) {
                // We need to check to see if the instance is healthy.
                if (marathonInstance.getTaskStatus() == TaskStatus.TASK_RUNNING) {
                    boolean createService = false;

                    if (!mMarathonService.containsHealthChecks(marathonInstance.getAppId())) {
                        createService = true;

                    } else {
                        // We need to check the health of the instance first.
                        Task task = mMarathonService.getTaskById(marathonInstance.getId());

                        for (HealthCheckResult check : task.getHealthCheckResults()) {

                            if (!check.isAlive()) {
                                createService = false;
                                break;
                            }

                        }

                    }

                    if (createService) {
                        log.info("Creating missing service: "+marathonInstance);
                        mConsulService.createNode(marathonInstance);
                    }

                }

            }

        }

    }

}
