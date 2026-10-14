package com.appium.constants;

import com.appium.config.ConfigFactory;
import com.appium.config.Configuration;
import com.appium.helpers.SystemHelpers;

import java.io.File;
import java.nio.file.Paths;

/**
 * FrameworkConstants là nơi tập trung các hằng số (constants) cho toàn bộ hệ thống Mobile Automation.
 * Cấu hình được nạp Type-safe thông qua thư viện Owner (Configuration & ConfigFactory) - chuẩn CRM-frameWork.
 */
public final class FrameworkConstants {

    private FrameworkConstants() {
    }

    private static final Configuration CONFIG = ConfigFactory.getConfigs();

    // ===== ĐƯỜNG DẪN DỰ ÁN & HỆ THỐNG =====
    public static final String PROJECT_PATH = SystemHelpers.getCurrentDir();

    // ===== CẤU HÌNH MÔI TRƯỜNG & THIẾT BỊ (Đọc Type-safe từ Owner) =====
    public static final String TARGET = CONFIG.TARGET();
    public static final String PLATFORM = CONFIG.PLATFORM();
    public static final String APPIUM_URL = CONFIG.APPIUM_URL();
    public static final String REMOTE_URL = CONFIG.REMOTE_URL();
    public static final String REMOTE_PORT = CONFIG.REMOTE_PORT();

    // ===== 1. Android Emulator =====
    public static final String ANDROID_EMULATOR_UDID = CONFIG.ANDROID_EMULATOR_UDID();
    public static final String ANDROID_EMULATOR_DEVICE_NAME = CONFIG.ANDROID_EMULATOR_DEVICE_NAME();
    public static final String ANDROID_EMULATOR_APP = CONFIG.ANDROID_EMULATOR_APP().trim().isEmpty() ? "" : Paths.get(PROJECT_PATH, CONFIG.ANDROID_EMULATOR_APP().trim()).toString();

    // ===== 2. Android Real Device =====
    public static final String ANDROID_REAL_UDID = CONFIG.ANDROID_REAL_UDID();
    public static final String ANDROID_REAL_DEVICE_NAME = CONFIG.ANDROID_REAL_DEVICE_NAME();
    public static final String ANDROID_REAL_APP = CONFIG.ANDROID_REAL_APP().trim().isEmpty() ? "" : Paths.get(PROJECT_PATH, CONFIG.ANDROID_REAL_APP().trim()).toString();

    // Android Common / Fallback
    public static final String ANDROID_UDID = CONFIG.ANDROID_UDID();
    public static final String ANDROID_DEVICE_NAME = CONFIG.ANDROID_DEVICE_NAME();
    public static final String ANDROID_APP = CONFIG.ANDROID_APP().trim().isEmpty() ? "" : Paths.get(PROJECT_PATH, CONFIG.ANDROID_APP().trim()).toString();
    public static final String ANDROID_APP_PACKAGE = CONFIG.ANDROID_APP_PACKAGE();
    public static final String ANDROID_APP_ACTIVITY = CONFIG.ANDROID_APP_ACTIVITY();
    public static final int ANDROID_SYSTEM_PORT = CONFIG.ANDROID_SYSTEM_PORT();
    public static final boolean AUTO_GRANT_PERMISSIONS = CONFIG.ANDROID_AUTO_GRANT_PERMISSIONS();
    public static final boolean NO_RESET = CONFIG.ANDROID_NO_RESET();
    public static final boolean FULL_RESET = CONFIG.ANDROID_FULL_RESET();
    public static final boolean ANDROID_IS_HEADLESS = CONFIG.ANDROID_IS_HEADLESS();

    // ===== 3. iOS Simulator =====
    public static final String IOS_SIM_UDID = CONFIG.IOS_SIM_UDID();
    public static final String IOS_SIM_DEVICE_NAME = CONFIG.IOS_SIM_DEVICE_NAME();
    public static final String IOS_SIM_APP = CONFIG.IOS_SIM_APP().trim().isEmpty() ? "" : Paths.get(PROJECT_PATH, CONFIG.IOS_SIM_APP().trim()).toString();

    // ===== 4. iOS Real Device =====
    public static final String IOS_REAL_UDID = CONFIG.IOS_REAL_UDID();
    public static final String IOS_REAL_DEVICE_NAME = CONFIG.IOS_REAL_DEVICE_NAME();
    public static final String IOS_REAL_APP = CONFIG.IOS_REAL_APP().trim().isEmpty() ? "" : Paths.get(PROJECT_PATH, CONFIG.IOS_REAL_APP().trim()).toString();
    public static final String IOS_REAL_BUNDLE_ID = CONFIG.IOS_REAL_BUNDLE_ID();

    // iOS Common / Fallback
    public static final String IOS_UDID = CONFIG.IOS_UDID();
    public static final String IOS_DEVICE_NAME = CONFIG.IOS_DEVICE_NAME();
    public static final String IOS_APP = CONFIG.IOS_APP().trim().isEmpty() ? "" : Paths.get(PROJECT_PATH, CONFIG.IOS_APP().trim()).toString();
    public static final String IOS_BUNDLE_ID = CONFIG.IOS_BUNDLE_ID();
    public static final int IOS_WDA_LOCAL_PORT = CONFIG.IOS_WDA_LOCAL_PORT();
    public static final boolean IOS_IS_HEADLESS = CONFIG.IOS_IS_HEADLESS();

    // Timeout (Seconds)
    public static final long EXPLICIT_TIMEOUT = CONFIG.TIMEOUT_EXPLICIT();
    public static final long PAGE_LOAD_TIMEOUT = CONFIG.TIMEOUT_PAGE_LOAD();
    public static final long APPIUM_COMMAND_TIMEOUT = CONFIG.TIMEOUT_APPIUM_COMMAND();
    public static final long DEVICE_STARTUP_TIMEOUT = CONFIG.TIMEOUT_DEVICE_STARTUP();

    // Features
    public static final boolean RECORD_VIDEO_ON_FAILURE = CONFIG.RECORD_VIDEO_ON_FAILURE();
    public static final boolean SCREENSHOT_ON_FAILURE = CONFIG.SCREENSHOT_ON_FAILURE();
    public static final boolean SCREENSHOT_ON_PASS = CONFIG.SCREENSHOT_ON_PASS();
    public static final int RETRY_COUNT = CONFIG.RETRY_COUNT();

    // Đường dẫn xuất báo cáo / hình ảnh / video
    public static final String EXPORT_CAPTURE_PATH = PROJECT_PATH + "exports" + File.separator + "ExportData" + File.separator + "Images";
    public static final String EXPORT_VIDEO_PATH = PROJECT_PATH + "exports" + File.separator + "ExportData" + File.separator + "Videos";

    // ===== EXTENT REPORTS CẤU HÌNH (Chuẩn CRM-frameWork) =====
    public static final String AUTHOR = CONFIG.AUTHOR();
    public static final String PROJECT_NAME = CONFIG.PROJECT_NAME();
    public static final String REPORT_TITLE = CONFIG.REPORT_TITLE();
    public static final String EXTENT_REPORT_NAME = CONFIG.EXTENT_REPORT_NAME();
    public static final String EXTENT_REPORT_FOLDER = CONFIG.EXTENT_REPORT_FOLDER();
    public static final String EXTENT_REPORT_FOLDER_PATH = PROJECT_PATH + EXTENT_REPORT_FOLDER;
    public static final String EXTENT_REPORT_FILE_NAME = EXTENT_REPORT_NAME + ".html";
    public static final String EXTENT_REPORT_FILE_PATH = EXTENT_REPORT_FOLDER_PATH + File.separator + EXTENT_REPORT_FILE_NAME;
    public static final String OVERRIDE_REPORTS = CONFIG.OVERRIDE_REPORTS();

    /**
     * Hàm tiện ích đọc cấu hình động (hỗ trợ tương thích ngược)
     */
    public static String get(String key) {
        String prop = System.getProperty(key);
        return prop != null ? prop : CONFIG.getProperty(key, "");
    }

    public static String get(String key, String defaultValue) {
        String prop = System.getProperty(key);
        return prop != null ? prop : CONFIG.getProperty(key, defaultValue);
    }
}
