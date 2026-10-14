package com.appium.driver;

import com.appium.constants.FrameworkConstants;
import com.appium.utils.LogUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.remote.options.BaseOptions;

import java.io.File;
import java.net.URL;
import java.time.Duration;

/**
 * DeviceFactory là Enum cài đặt Factory Pattern chịu trách nhiệm sản xuất
 * AppiumDriver
 * và Options cho từng hệ điều hành di động (ANDROID và IOS).
 * Tự động phân giải thông số tương ứng cho máy thật (Real Device) hoặc máy ảo
 * (Emulator/Simulator).
 */
public enum DeviceFactory {

    ANDROID {
        @Override
        public AppiumDriver createDriver(String udid, int systemPort) {
            try {
                URL localUrl = new URL(FrameworkConstants.APPIUM_URL);
                LogUtils.info(String.format("DeviceFactory -> Khởi tạo Local AndroidDriver: UDID=%s, Port=%d", udid,
                        systemPort));
                return new AndroidDriver(localUrl, getOptions(udid, systemPort));
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khởi tạo Local AndroidDriver với URL: " + FrameworkConstants.APPIUM_URL,
                        e);
            }
        }

        @Override
        public UiAutomator2Options getOptions(String udid, int systemPort) {
            validateUdid(udid, "Android");
            boolean isRealDevice = isAndroidRealDevice(udid);
            String deviceName = resolveAndroidDeviceName(isRealDevice);
            String appPath = resolveAndroidAppPath(isRealDevice);
            UiAutomator2Options options = new UiAutomator2Options()
                    .setDeviceName(deviceName)
                    .setUdid(udid)
                    .setSystemPort(systemPort)
                    .setAutoGrantPermissions(FrameworkConstants.AUTO_GRANT_PERMISSIONS)
                    .setNoReset(FrameworkConstants.NO_RESET)
                    .setIsHeadless(FrameworkConstants.ANDROID_IS_HEADLESS)
                    .setAvdLaunchTimeout(Duration.ofSeconds(FrameworkConstants.DEVICE_STARTUP_TIMEOUT))
                    .setAvdReadyTimeout(Duration.ofSeconds(FrameworkConstants.DEVICE_STARTUP_TIMEOUT))
                    .setNewCommandTimeout(Duration.ofSeconds(FrameworkConstants.APPIUM_COMMAND_TIMEOUT));

            setupAndroidApplication(options, appPath);
            return options;
        }
    },

    IOS {
        @Override
        public AppiumDriver createDriver(String udid, int wdaLocalPort) {
            try {
                URL localUrl = new URL(FrameworkConstants.APPIUM_URL);
                LogUtils.info(String.format("DeviceFactory -> Khởi tạo Local IOSDriver: UDID=%s, Port=%d", udid,
                        wdaLocalPort));
                return new IOSDriver(localUrl, getOptions(udid, wdaLocalPort));
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khởi tạo Local IOSDriver với URL: " + FrameworkConstants.APPIUM_URL, e);
            }
        }

        @Override
        public XCUITestOptions getOptions(String udid, int wdaLocalPort) {
            validateUdid(udid, "iOS");
            boolean isRealDevice = isIosRealDevice(udid);
            String deviceName = resolveIosDeviceName(isRealDevice);
            String appPath = resolveIosAppPath(isRealDevice);
            String bundleId = resolveIosBundleId(isRealDevice);
            XCUITestOptions options = new XCUITestOptions()
                    .setDeviceName(deviceName)
                    .setUdid(udid)
                    .setWdaLocalPort(wdaLocalPort)
                    .setConnectHardwareKeyboard(false)
                    .setAutoAcceptAlerts(true)
                    .setIsHeadless(FrameworkConstants.IOS_IS_HEADLESS)
                    .setSimulatorStartupTimeout(Duration.ofSeconds(FrameworkConstants.DEVICE_STARTUP_TIMEOUT))
                    .setNewCommandTimeout(Duration.ofSeconds(FrameworkConstants.APPIUM_COMMAND_TIMEOUT));

            setupIosApplication(options, appPath, bundleId);
            return options;
        }
    };

    // =========================================================================
    // ABSTRACT METHODS
    // =========================================================================
    public abstract AppiumDriver createDriver(String udid, int port);

    public abstract BaseOptions<?> getOptions(String udid, int port);

    // =========================================================================
    // STATIC HELPER METHODS - RESOLUTION & FACTORY CONVENIENCE
    // =========================================================================

    /**
     * Phân giải nhanh DeviceFactory từ chuỗi platform (android_emulator, ios_real,
     * ...).
     */
    public static DeviceFactory fromPlatform(String platform) {
        if (platform != null && platform.trim().toLowerCase().startsWith("ios")) {
            return IOS;
        }
        return ANDROID;
    }

    private static void validateUdid(String udid, String platformName) {
        if (udid == null || udid.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("❌ UDID thiết bị %s không được để trống khi khởi tạo Driver!", platformName));
        }
    }

    // --- ANDROID RESOLVERS ---

    private static boolean isAndroidRealDevice(String udid) {
        return udid.equalsIgnoreCase(FrameworkConstants.ANDROID_REAL_UDID)
                || FrameworkConstants.PLATFORM.equalsIgnoreCase("android_real");
    }

    private static String resolveAndroidDeviceName(boolean isRealDevice) {
        if (isRealDevice && !FrameworkConstants.ANDROID_REAL_DEVICE_NAME.isEmpty()) {
            return FrameworkConstants.ANDROID_REAL_DEVICE_NAME;
        }
        if (!isRealDevice && !FrameworkConstants.ANDROID_EMULATOR_DEVICE_NAME.isEmpty()) {
            return FrameworkConstants.ANDROID_EMULATOR_DEVICE_NAME;
        }
        return FrameworkConstants.ANDROID_DEVICE_NAME;
    }

    private static String resolveAndroidAppPath(boolean isRealDevice) {
        if (isRealDevice && !FrameworkConstants.ANDROID_REAL_APP.isEmpty()) {
            return FrameworkConstants.ANDROID_REAL_APP;
        }
        if (!isRealDevice && !FrameworkConstants.ANDROID_EMULATOR_APP.isEmpty()) {
            return FrameworkConstants.ANDROID_EMULATOR_APP;
        }
        return FrameworkConstants.ANDROID_APP;
    }

    private static void setupAndroidApplication(UiAutomator2Options options, String appPath) {
        if (!FrameworkConstants.ANDROID_APP_PACKAGE.isEmpty()) {
            options.setAppPackage(FrameworkConstants.ANDROID_APP_PACKAGE);
        }
        if (!FrameworkConstants.ANDROID_APP_ACTIVITY.isEmpty()) {
            options.setAppActivity(FrameworkConstants.ANDROID_APP_ACTIVITY);
        }

        if (appPath != null && !appPath.trim().isEmpty()) {
            File appFile = new File(appPath);
            if (!appFile.exists() || !appFile.isFile()) {
                throw new IllegalArgumentException("❌ Không tìm thấy file APK tại: " + appPath);
            }
            LogUtils.info("Tìm thấy file APK tại: " + appFile.getAbsolutePath());
            options.setApp(appFile.getAbsolutePath());
        } else if (FrameworkConstants.ANDROID_APP_PACKAGE.isEmpty()
                || FrameworkConstants.ANDROID_APP_ACTIVITY.isEmpty()) {
            throw new IllegalArgumentException(
                    "❌ Cấu hình ứng dụng Android bị trống! Bạn phải cung cấp file APK hợp lệ hoặc Package/Activity.");
        } else {
            LogUtils.info("Mở ứng dụng Android đã cài sẵn theo package: " + FrameworkConstants.ANDROID_APP_PACKAGE);
        }
    }

    // --- IOS RESOLVERS ---

    private static boolean isIosRealDevice(String udid) {
        return udid.equalsIgnoreCase(FrameworkConstants.IOS_REAL_UDID)
                || FrameworkConstants.PLATFORM.equalsIgnoreCase("ios_real");
    }

    private static String resolveIosDeviceName(boolean isRealDevice) {
        if (isRealDevice && !FrameworkConstants.IOS_REAL_DEVICE_NAME.isEmpty()) {
            return FrameworkConstants.IOS_REAL_DEVICE_NAME;
        }
        if (!isRealDevice && !FrameworkConstants.IOS_SIM_DEVICE_NAME.isEmpty()) {
            return FrameworkConstants.IOS_SIM_DEVICE_NAME;
        }
        return FrameworkConstants.IOS_DEVICE_NAME;
    }

    private static String resolveIosAppPath(boolean isRealDevice) {
        if (isRealDevice && !FrameworkConstants.IOS_REAL_APP.isEmpty()) {
            return FrameworkConstants.IOS_REAL_APP;
        }
        if (!isRealDevice && !FrameworkConstants.IOS_SIM_APP.isEmpty()) {
            return FrameworkConstants.IOS_SIM_APP;
        }
        return FrameworkConstants.IOS_APP;
    }

    private static String resolveIosBundleId(boolean isRealDevice) {
        if (isRealDevice && !FrameworkConstants.IOS_REAL_BUNDLE_ID.isEmpty()) {
            return FrameworkConstants.IOS_REAL_BUNDLE_ID;
        }
        return FrameworkConstants.IOS_BUNDLE_ID;
    }

    private static void setupIosApplication(XCUITestOptions options, String appPath, String bundleId) {
        if (bundleId != null && !bundleId.isEmpty()) {
            options.setBundleId(bundleId);
        }

        if (appPath != null && !appPath.trim().isEmpty()) {
            File appFile = new File(appPath);
            if (!appFile.exists()) {
                throw new IllegalArgumentException("❌ Không tìm thấy file iOS App/IPA tại: " + appPath);
            }
            LogUtils.info("Tìm thấy file iOS App tại: " + appFile.getAbsolutePath());
            options.setApp(appFile.getAbsolutePath());
        } else if (bundleId == null || bundleId.isEmpty()) {
            throw new IllegalArgumentException(
                    "❌ Cấu hình ứng dụng iOS bị trống! Bạn phải cung cấp file App/IPA hoặc Bundle ID.");
        } else {
            LogUtils.info("Mở ứng dụng iOS theo Bundle ID: " + bundleId);
        }
    }
}
