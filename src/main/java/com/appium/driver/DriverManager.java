package com.appium.driver;

import io.appium.java_client.AppiumDriver;

/**
 * DriverManager là class cốt lõi để quản lý các phiên bản (instances) của AppiumDriver.
 * Sử dụng Design Pattern Singleton kết hợp với ThreadLocal.
 * Mục đích: Đảm bảo khi chạy test song song (parallel trên nhiều thiết bị), mỗi luồng (thread)
 * sẽ có một AppiumDriver riêng biệt, không bị xung đột (conflict) - chuẩn CRM-frameWork.
 */
public final class DriverManager {

    // ThreadLocal lưu trữ AppiumDriver riêng biệt cho từng luồng (thread)
    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    private DriverManager() {
        super();
    }

    /**
     * Lấy AppiumDriver của luồng hiện tại.
     * @return AppiumDriver đang chạy trong thread hiện tại.
     */
    public static AppiumDriver getDriver() {
        return driver.get();
    }

    /**
     * Thiết lập AppiumDriver cho luồng hiện tại.
     * Thường được gọi trong @BeforeMethod của BaseTest ngay sau khi tạo driver.
     * @param appiumDriver AppiumDriver instance cần gán cho thread này.
     */
    public static void setDriver(AppiumDriver appiumDriver) {
        DriverManager.driver.set(appiumDriver);
    }

    /**
     * Đóng session Appium của luồng hiện tại và giải phóng bộ nhớ.
     * Thường được gọi trong @AfterMethod của BaseTest.
     */
    public static void quit() {
        if (DriverManager.getDriver() != null) {
            try {
                DriverManager.getDriver().quit();
            } finally {
                driver.remove();
            }
        }
    }

    /**
     * Tương thích ngược với các hàm gọi quitDriver()
     */
    public static void quitDriver() {
        quit();
    }
}
