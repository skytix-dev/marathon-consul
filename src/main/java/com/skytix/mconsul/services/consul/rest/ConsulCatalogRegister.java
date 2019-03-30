package com.skytix.mconsul.services.consul.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by xfire on 10/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsulCatalogRegister {
    private String mId;
    private String mName;
    private String mAddress;
    private int mPort;

    public ConsulCatalogRegister() {
    }

    public ConsulCatalogRegister(String aId, String aName, String aAddress, int aPort) {
        mId = aId;
        mName = aName;
        mAddress = aAddress;
        mPort = aPort;
    }

    @JsonProperty("ID")
    public String getId() {
        return mId;
    }

    @JsonProperty("Name")
    public String getName() {
        return mName;
    }

    @JsonProperty("Address")
    public String getAddress() {
        return mAddress;
    }

    @JsonProperty("Port")
    public int getPort() {
        return mPort;
    }

    public void setId(String aId) {
        mId = aId;
    }

    public void setName(String aName) {
        mName = aName;
    }

    public void setAddress(String aAddress) {
        mAddress = aAddress;
    }

    public void setPort(int aPort) {
        mPort = aPort;
    }
}
