package com.appium.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LogUtils {
    private static final Logger LOGGER = LogManager.getLogger(LogUtils.class);

    private LogUtils() {}

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void info(Object object) {
        LOGGER.info(object);
    }

    public static void warn(String message) {
        LOGGER.warn(message);
    }

    public static void error(String message) {
        LOGGER.error(message);
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }

    public static void fatal(String message) {
        LOGGER.fatal(message);
    }

    public static void debug(String message) {
        LOGGER.debug(message);
    }
}
