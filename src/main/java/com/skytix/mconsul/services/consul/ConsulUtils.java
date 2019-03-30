package com.skytix.mconsul.services.consul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xfire on 10/10/2015.
 */
public class ConsulUtils {
    private static final Logger log = LoggerFactory.getLogger(ConsulUtils.class);
    private static final String portIndexPattern = "^(.*?)-port([0-9])+$";
    /**
     * Get the port index from a consul service name.
     * @param aServiceName Service name in consul
     * @return Port index of the application.
     */
    public static int getPortIndex(String aServiceName) {

        if (aServiceName.matches(portIndexPattern)) {
            final Matcher m = Pattern.compile(portIndexPattern).matcher(aServiceName);

            if (m.matches()) {
                return Integer.parseInt(m.group(2));

            } else {
                log.warn("Unable to determine port index from service name: "+aServiceName);
                return 0;
            }

        } else {
            return 0;
        }

    }

    public static String getAppNamePart(String aServiceName) {

        if (aServiceName.matches(portIndexPattern)) {
            final Matcher m = Pattern.compile(portIndexPattern).matcher(aServiceName);

            if (m.matches()) {
                return m.group(1);

            } else {
                log.warn("Unable to determine app name from service name: "+aServiceName);
                return aServiceName;
            }

        } else {
            return aServiceName;
        }

    }

}
