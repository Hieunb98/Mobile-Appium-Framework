package com.appium.reports;

import com.aventstack.extentreports.ExtentTest;

/**
 * ExtentTestManager quản lý thể hiện ExtentTest cho từng luồng thực thi (ThreadLocal) - chuẩn CRM-frameWork.
 */
public final class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private ExtentTestManager() {
    }

    public static ExtentTest getExtentTest() {
        return extentTest.get();
    }

    public static void setExtentTest(ExtentTest test) {
        extentTest.set(test);
    }

    public static void unload() {
        extentTest.remove();
    }
}
