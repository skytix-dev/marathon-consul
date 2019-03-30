package com.skytix.mconsul.services.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;

/**
 * Created by xfire on 7/10/2015.
 */
@Component
public class ZooKeeperService {
    private static final Logger log = LoggerFactory.getLogger(ZooKeeperService.class);

    @Value("${zkHosts}")
    private String mConnectString;
    private CuratorFramework mCuratorFramework;

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        final RetryPolicy retryPolicy = new ExponentialBackoffRetry(5, 29);
        log.info("Connecting to ZooKeeper at - "+ mConnectString);
        mCuratorFramework = CuratorFrameworkFactory.newClient(mConnectString, retryPolicy);
        mCuratorFramework.start();
        mCuratorFramework.blockUntilConnected();
    }

    @PreDestroy
    public void destroy() {

        try {
            mCuratorFramework.close();

        } catch (Exception aE) {
            // Go bug someone else
        }

    }

    public boolean nodeExists(String aNodePath) throws ZooKeeperException {

        try {
            return mCuratorFramework.checkExists().forPath(aNodePath) != null;

        } catch (Exception aE) {
            throw new ZooKeeperException(aE);
        }

    }

    public List<String> getChildren(String aNodePath) throws ZooKeeperException {

        try {
            return mCuratorFramework.getChildren().forPath(aNodePath);

        } catch (Exception aE) {
            throw new ZooKeeperException(aE);
        }

    }

    public String getNode(String aNodePath) throws ZooKeeperException {

        try {
            return new String(mCuratorFramework.getData().forPath(aNodePath));

        } catch (Exception aE) {
            throw new ZooKeeperException(aE);
        }

    }

}
