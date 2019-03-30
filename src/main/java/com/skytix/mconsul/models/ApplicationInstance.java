package com.skytix.mconsul.models;

import com.skytix.mconsul.event.TaskStatus;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by xfire on 10/10/2015.
 */
public class ApplicationInstance implements Comparable {
    private final String mId;
    private final String mAppName;
    private final String mAppId;
    private final String mHostName;
    private final int[] mPorts;
    private final TaskStatus mTaskStatus;

    public ApplicationInstance(String aId, String aAppId, String aAppName, String aHostName, int[] aPorts, TaskStatus aTaskStatus) {
        mId = aId;
        mAppId = aAppId;
        mAppName = aAppName;
        mHostName = aHostName;
        mPorts = aPorts;
        mTaskStatus = aTaskStatus;
    }

    public String getId() {
        return mId;
    }

    public String getHostName() {
        return mHostName;
    }

    public int[] getPorts() {
        return mPorts;
    }

    public String getAppName() {
        return mAppName;
    }

    public String getAppId() {
        return mAppId;
    }

    public TaskStatus getTaskStatus() {
        return mTaskStatus;
    }

    @Override
    public int compareTo(Object o) {
        final ApplicationInstance appInstance = (ApplicationInstance)o;

        return new CompareToBuilder()
            .append(mHostName, appInstance.mHostName)
            .append(mPorts, appInstance.mPorts)
            .toComparison();
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(mId, ((ApplicationInstance) obj).getId()).build();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId).build();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", mId)
            .append("appName", mAppName)
            .append("hostName", mHostName)
            .append("ports", mPorts)
            .toString();
    }
}
