package com.skytix.mconsul.event;

import com.skytix.mconsul.utils.Version;

/**
 * Created by xfire on 8/10/2015.
 */
public interface MarathonEventHandler<T extends MarathonEvent> {
    public void handle(T aEvent, Version aMarathonVersion);
}
