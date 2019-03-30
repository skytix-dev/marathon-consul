package com.skytix.mconsul.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTime;

/**
 * Created by xfire on 8/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface BaseMarathonEvent extends MarathonEvent {
    public String getEventType();
    public DateTime getTimestamp();
    public String getAppId();
}
