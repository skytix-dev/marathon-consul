package com.skytix.mconsul.services.marathon.rest;

import feign.Param;
import feign.RequestLine;

/**
 * Created by marcde on 9/10/2015.
 */
public interface MarathonApi {
    @RequestLine("GET /v2/apps/{appId}")
    public MarathonAppLookup getApplication(@Param("appId") String aAppId);

    @RequestLine("GET /v2/apps?embed=apps.tasks")
    public ApplicationList getApplications();

    @RequestLine("GET /v2/tasks")
    public TaskList getTasks();

    @RequestLine("GET /v2/apps/{appId}/tasks")
    public TaskList getAppTasks(@Param("appId") String aAppId);

    @RequestLine("GET /v2/leader")
    public MarathonLeader getLeader();

    @RequestLine("GET /v2/info")
    public ServerInfo getServerInfo();

    @RequestLine("GET /ping")
    public void ping();

}
