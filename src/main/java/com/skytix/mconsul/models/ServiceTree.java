package com.skytix.mconsul.models;

import com.skytix.mconsul.services.consul.rest.ServiceNode;
import com.skytix.mconsul.services.marathon.rest.MarathonApplication;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by marcde on 9/10/2015.
 *
 * The ServiceTree is a model that represents the host and port information for our applications.  As we map both the configurations of Marathon and Consul into this model, it allows us to easily
 * compare the differences and apply changes.
 */
public class ServiceTree {
    private final List<Application> mApplications = new ArrayList<>();

    public ServiceTree(MarathonApplication... aMarathonApplications) {

        for (MarathonApplication application : aMarathonApplications) {
            mApplications.add(new Application(application));
        }

        Collections.sort(mApplications);
    }

    public ServiceTree(Map<String, List<ServiceNode>> aConsulNodes) {

        for (String serviceName : aConsulNodes.keySet()) {
            mApplications.add(new Application(serviceName, aConsulNodes.get(serviceName)));
        }

        Collections.sort(mApplications);
    }

    public List<Application> getApplications() {
        return mApplications;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mApplications).hashCode();
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

        ServiceTree rhs = (ServiceTree) obj;
        return new EqualsBuilder().append(mApplications, rhs).isEquals();
    }

}
