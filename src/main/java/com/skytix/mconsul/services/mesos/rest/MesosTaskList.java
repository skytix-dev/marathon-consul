package com.skytix.mconsul.services.mesos.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by marc on 13/11/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface MesosTaskList {
    public List<MesosTask> getTasks();
}
