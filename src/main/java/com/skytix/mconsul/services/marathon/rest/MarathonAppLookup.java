package com.skytix.mconsul.services.marathon.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by xfire on 11/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface MarathonAppLookup {
    public MarathonApplication getApp();
}
