package com.skytix.mconsul.services.consul.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by xfire on 10/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsulCatalogRegister {
    private String mId;
    private String mName;
    private String mAddress;
    private int mPort;
    private Map<String, String> mMeta;

    public ConsulCatalogRegister() {
    }

    public ConsulCatalogRegister(String mId, String mName, String mAddress, int mPort, Map<String, String> mMeta) {
        this.mId = mId;
        this.mName = mName;
        this.mAddress = mAddress;
        this.mPort = mPort;
        this.mMeta = mMeta;
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

    @JsonProperty("Meta")
    public Map<String, String> getMeta() {
        return mMeta;
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

    public void setMeta(Map<String, String> aMeta) {
        mMeta = aMeta;
    }
}
