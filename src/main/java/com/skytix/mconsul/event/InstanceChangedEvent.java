package com.skytix.mconsul.event;

/**
 * Created by marc on 31/03/2017.
 */
public interface InstanceChangedEvent extends BaseMarathonEvent {
    public String getInstanceId();
    public String getCondition();
    public String getRunSpecId();
    public String getAgentId();

    public default boolean isTerminal() {

        switch (getCondition().toLowerCase()) {
            case "created":
            case "starting":
            case "started":
            case "staging":
            case "running":
            case "lost":
                return false;

            default:
                return true;

        }

    }

}
