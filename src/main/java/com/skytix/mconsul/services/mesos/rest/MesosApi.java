package com.skytix.mconsul.services.mesos.rest;

import feign.RequestLine;

/**
 * Created by marc on 13/11/2016.
 */
public interface MesosApi {

    @RequestLine("GET /master/tasks")
    public MesosTaskList getTasks();

}
