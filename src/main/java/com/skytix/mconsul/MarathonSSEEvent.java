package com.skytix.mconsul;

import com.skytix.mconsul.event.MarathonEvent;
import com.skytix.mconsul.event.MarathonEventType;

/**
 * Created by marcde on 13/10/2015.
 */
public class MarathonSSEEvent {
    private MarathonEvent mMarathonEvent;
    private MarathonEventType mMarathonEventType;

    public MarathonEvent getMarathonEvent() {
        return mMarathonEvent;
    }

    public void setMarathonEvent(MarathonEvent aMarathonEvent) {
        mMarathonEvent = aMarathonEvent;
    }

    public MarathonEventType getMarathonEventType() {
        return mMarathonEventType;
    }

    public void setMarathonEventType(MarathonEventType aMarathonEventType) {
        mMarathonEventType = aMarathonEventType;
    }

}
