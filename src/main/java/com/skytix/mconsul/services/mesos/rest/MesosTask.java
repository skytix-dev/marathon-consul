package com.skytix.mconsul.services.mesos.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by marc on 13/11/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface MesosTask {
    public String getId();
    public String getName();
    public String getState();
}
