package com.appium.config;

import org.aeonbits.owner.ConfigCache;

/**
 * Factory class cung cấp thể hiện duy nhất của Configuration (chuẩn CRM-frameWork).
 */
public final class ConfigFactory {

    private ConfigFactory() {
    }

    public static Configuration getConfigs() {
        return ConfigCache.getOrCreate(Configuration.class);
    }
}
