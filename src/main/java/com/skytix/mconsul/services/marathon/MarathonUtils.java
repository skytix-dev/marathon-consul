package com.skytix.mconsul.services.marathon;

import com.skytix.mconsul.models.ApplicationInstance;
import com.skytix.mconsul.services.marathon.rest.Task;
import com.skytix.mconsul.utils.Version;

import java.util.function.Predicate;

/**
 * Created by xfire on 10/10/2015.
 */
public class MarathonUtils {

    /**
     * Translates a marathon appId name to an application name that is DNS compatible.
     * @param aMarathonAppId
     * @return
     */
    public static String parseAppName(String aMarathonAppId) {

        if (aMarathonAppId.startsWith("/")) {
            return replaceContents(aMarathonAppId.substring(1, aMarathonAppId.length()));

        } else {
            return replaceContents(aMarathonAppId);
        }

    }

    public static String extractInstanceId(String aHealthCheckInstanceId) {

        if (aHealthCheckInstanceId.startsWith("instance [")) {
            return aHealthCheckInstanceId.substring(10, aHealthCheckInstanceId.length() - 1);

        } else {
            return aHealthCheckInstanceId;
        }

    }


    public static ApplicationInstance buildAppInstance(Task aTask) {
        return new ApplicationInstance(aTask.getId(), aTask.getAppId(), parseAppName(aTask.getAppId()), aTask.getHost(), aTask.getPorts(), aTask.getState());
    }

    /**
     * Replace all spaces, forward slashes and underscores to hyphens.
     * @param aInput
     * @return
     */
    private static String replaceContents(String aInput) {
        return aInput.replaceAll("\\s","-")
            .replaceAll("/", "-")
            .replaceAll("_", "-");
    }

    public static Predicate<Version> pGreaterThan(Version aVersion) {
        return (version) -> version.compareTo(aVersion) > 0;
    }

    public static Predicate<Version> pGreaterThanOrEq(Version aVersion) {
        return pOr(pGreaterThan(aVersion), pEquals(aVersion));
    }

    public static Predicate<Version> pLessThan(Version aVersion) {
        return (version) -> version.compareTo(aVersion) < 0;
    }

    public static Predicate<Version> pLessThanOrEq(Version aVersion) {
        return pOr(pLessThan(aVersion), pEquals(aVersion));
    }

    public static Predicate<Version> pEquals(Version aVersion) {
        return (version) -> version.compareTo(aVersion) == 0;
    }

    public static Predicate<Version> pOr(Predicate<Version>... aPredicates) {

        return (version) -> {

            for (Predicate<Version> predicate : aPredicates) {

                if (predicate.test(version)) {
                    return true;
                }

            }

            return false;
        };

    }

}
