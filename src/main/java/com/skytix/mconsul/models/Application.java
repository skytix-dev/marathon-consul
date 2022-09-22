package com.skytix.mconsul.models;

import com.skytix.mconsul.event.TaskStatus;
import com.skytix.mconsul.services.consul.ConsulUtils;
import com.skytix.mconsul.services.consul.rest.ServiceNode;
import com.skytix.mconsul.services.marathon.MarathonUtils;
import com.skytix.mconsul.services.marathon.rest.MarathonApplication;
import com.skytix.mconsul.services.marathon.rest.Task;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

/**
 * Created by xfire on 9/10/2015.
 */
public class Application implements Comparable {
    private final String mAppId;
    private final String mAppName;
    private final Map<String, String> mAppLabels;
    private final List<ApplicationInstance> mInstances = new ArrayList<>();

    public Application(MarathonApplication aApplication) {
        mAppId = aApplication.getId();
        mAppName = MarathonUtils.parseAppName(aApplication.getId());
        mAppLabels = aApplication.getLabels();

        for (Task task : aApplication.getTasks()) {
            mInstances.add(new ApplicationInstance(task.getId(), mAppId, mAppName, task.getHost(), task.getPorts(), task.getState(), mAppLabels));
        }

        Collections.sort(mInstances);
    }

    /**
     * Creates a new instance from a consul service name and list of Nodes.
     * @param aServiceName Service name
     * @param aConsulNodes List of ServiceNodes for the
     */
    public Application(String aServiceName, List<ServiceNode> aConsulNodes) {
        mAppId = aServiceName;
        mAppName = aServiceName;

        if (aConsulNodes.isEmpty()) {
            mAppLabels = Collections.emptyMap();

        } else {
            mAppLabels = aConsulNodes.get(0).getServiceMeta();
        }

        final Map<String, Map<Integer, Integer>> serviceNodePorts = new HashMap<>();
        final Map<String, List<ServiceNode>> nodesByServiceId = new HashMap<>();

        for (ServiceNode node : aConsulNodes) {
            final int portIndex = ConsulUtils.getPortIndex(node.getServiceName());
            final int port = node.getServicePort();
            final String instanceId = ConsulUtils.getAppNamePart(node.getServiceID());

            if (!serviceNodePorts.containsKey(instanceId)) {
                serviceNodePorts.put(instanceId, new HashMap<>());
            }

            if (!nodesByServiceId.containsKey(instanceId)) {
                nodesByServiceId.put(instanceId, new ArrayList<>());
            }

            serviceNodePorts.get(instanceId).put(portIndex, port);
            nodesByServiceId.get(instanceId).add(node);
        }

        for (String instanceId : nodesByServiceId.keySet()) {
            final List<ServiceNode> nodes = nodesByServiceId.get(instanceId);
            final Map<Integer, Integer> ports = serviceNodePorts.get(instanceId);

            final int[] portArray = new int[ports.size()];

            for (Integer portIndex : ports.keySet()) {
                portArray[portIndex < portArray.length ? portIndex : portArray.length -1] = ports.get(portIndex);
            }

            mInstances.add(new ApplicationInstance(instanceId, mAppId, mAppName, nodes.get(0).getServiceAddress(), portArray, TaskStatus.TASK_RUNNING, mAppLabels));
        }

        Collections.sort(mInstances);
    }

    public String getAppName() {
        return mAppName;
    }

    public String getAppId() {
        return mAppId;
    }

    public List<ApplicationInstance> getInstances() {
        return mInstances;
    }

    @Override
    public int compareTo(Object o) {
        final Application app = (Application)o;

        return new CompareToBuilder()
            .append(mAppName, app.mAppName)
            .append(mInstances, app.mInstances)
            .toComparison();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj.getClass() != getClass()) {
            return false;
        }

        final Application rhs = (Application) obj;

        return new EqualsBuilder()
            .append(this.mAppName, rhs.mAppName)
            .isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder()
            .append(mAppName)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("mAppName", mAppName)
            .toString();
    }
}
