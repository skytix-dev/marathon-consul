package com.skytix.mconsul.services.zookeeper;

/**
 * Created by xfire on 7/10/2015.
 */
public class ZooKeeperException extends Exception {

    public ZooKeeperException() {
    }

    public ZooKeeperException(String message) {
        super(message);
    }

    public ZooKeeperException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZooKeeperException(Throwable cause) {
        super(cause);
    }
}
