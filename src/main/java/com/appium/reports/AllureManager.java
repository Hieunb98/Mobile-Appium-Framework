package com.appium.reports;

import io.qameta.allure.Attachment;

public final class AllureManager {
    private AllureManager() {}

    @Attachment(value = "Screenshot: {name}", type = "image/png")
    public static byte[] saveScreenshotPNG(String name, byte[] screenshot) {
        return screenshot;
    }

    @Attachment(value = "Video: {name}", type = "video/mp4")
    public static byte[] saveVideoMP4(String name, byte[] video) {
        return video;
    }

    @Attachment(value = "{name}", type = "text/plain")
    public static String saveTextLog(String name, String message) {
        return message;
    }

    @Attachment(value = "XML Source: {name}", type = "text/xml")
    public static String savePageSourceXML(String name, String xmlSource) {
        return xmlSource;
    }
}
