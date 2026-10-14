package com.appium.config;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

/**
 * Interface cấu hình hệ thống Mobile Automation sử dụng thư viện Owner (chuẩn CRM-frameWork).
 * Tự động hợp nhất (MERGE) cấu hình từ:
 * 1. Tham số dòng lệnh Maven / System Properties (-Dplatform=..., -Dtarget=...)
 * 2. Biến môi trường hệ thống (System Environment)
 * 3. File config.properties tại src/test/resources/config.properties
 */
@LoadPolicy(LoadType.MERGE)
@Sources({
        "system:properties",
        "system:env",
        "file:./src/test/resources/config.properties",
        "classpath:config.properties"
})
public interface Configuration extends Config, Accessible {

    // ===== MÔI TRƯỜNG & TARGET CHẠY =====
    @Key("TARGET")
    @DefaultValue("local")
    String TARGET();

    @Key("platform")
    @DefaultValue("android_emulator")
    String PLATFORM();

    @Key("appium.url")
    @DefaultValue("http://127.0.0.1:4723")
    String APPIUM_URL();

    @Key("REMOTE_URL")
    @DefaultValue("localhost")
    String REMOTE_URL();

    @Key("REMOTE_PORT")
    @DefaultValue("4444")
    String REMOTE_PORT();

    // ===== 1. CẤU HÌNH ANDROID EMULATOR (Máy ảo) =====
    @Key("android.emulator.udid")
    @DefaultValue("emulator-5554")
    String ANDROID_EMULATOR_UDID();

    @Key("android.emulator.deviceName")
    @DefaultValue("Medium_Phone_API_34")
    String ANDROID_EMULATOR_DEVICE_NAME();

    @Key("android.emulator.app")
    @DefaultValue("apps/mydemoapp.apk")
    String ANDROID_EMULATOR_APP();

    // ===== 2. CẤU HÌNH ANDROID REAL (Máy thật) =====
    @Key("android.real.udid")
    @DefaultValue("")
    String ANDROID_REAL_UDID();

    @Key("android.real.deviceName")
    @DefaultValue("My_Real_Android")
    String ANDROID_REAL_DEVICE_NAME();

    @Key("android.real.app")
    @DefaultValue("apps/mydemoapp.apk")
    String ANDROID_REAL_APP();

    // ===== CẤU HÌNH CHUNG CHO ANDROID (Fallback & Common) =====
    @Key("android.udid")
    @DefaultValue("emulator-5554")
    String ANDROID_UDID();

    @Key("android.deviceName")
    @DefaultValue("Medium_Phone_API_34")
    String ANDROID_DEVICE_NAME();

    @Key("android.app")
    @DefaultValue("apps/mydemoapp.apk")
    String ANDROID_APP();

    @Key("android.appPackage")
    @DefaultValue("com.saucelabs.mydemoapp.rn")
    String ANDROID_APP_PACKAGE();

    @Key("android.appActivity")
    @DefaultValue("com.saucelabs.mydemoapp.rn.MainActivity")
    String ANDROID_APP_ACTIVITY();

    @Key("android.systemPort")
    @DefaultValue("8200")
    int ANDROID_SYSTEM_PORT();

    @Key("android.autoGrantPermissions")
    @DefaultValue("true")
    boolean ANDROID_AUTO_GRANT_PERMISSIONS();

    @Key("android.noReset")
    @DefaultValue("true")
    boolean ANDROID_NO_RESET();

    @Key("android.fullReset")
    @DefaultValue("false")
    boolean ANDROID_FULL_RESET();

    @Key("android.isHeadless")
    @DefaultValue("false")
    boolean ANDROID_IS_HEADLESS();

    // ===== 3. CẤU HÌNH IOS SIMULATOR (Máy ảo) =====
    @Key("ios.sim.udid")
    @DefaultValue("373770D0-E601-48D6-ABAD-64BAA3B7B9A7")
    String IOS_SIM_UDID();

    @Key("ios.sim.deviceName")
    @DefaultValue("iPhone 17")
    String IOS_SIM_DEVICE_NAME();

    @Key("ios.sim.app")
    @DefaultValue("apps/MyDemoApp.app")
    String IOS_SIM_APP();

    // ===== 4. CẤU HÌNH IOS REAL (Máy thật / iPad) =====
    @Key("ios.real.udid")
    @DefaultValue("")
    String IOS_REAL_UDID();

    @Key("ios.real.deviceName")
    @DefaultValue("My_Real_iPhone")
    String IOS_REAL_DEVICE_NAME();

    @Key("ios.real.app")
    @DefaultValue("")
    String IOS_REAL_APP();

    @Key("ios.real.bundleId")
    @DefaultValue("com.saucelabs.mydemo.app.ios")
    String IOS_REAL_BUNDLE_ID();

    // ===== CẤU HÌNH CHUNG CHO IOS (Fallback & Common) =====
    @Key("ios.udid")
    @DefaultValue("")
    String IOS_UDID();

    @Key("ios.deviceName")
    @DefaultValue("iPhone Simulator")
    String IOS_DEVICE_NAME();

    @Key("ios.app")
    @DefaultValue("")
    String IOS_APP();

    @Key("ios.bundleId")
    @DefaultValue("com.saucelabs.mydemo.app.ios")
    String IOS_BUNDLE_ID();

    @Key("ios.wdaLocalPort")
    @DefaultValue("8100")
    int IOS_WDA_LOCAL_PORT();

    @Key("ios.isHeadless")
    @DefaultValue("false")
    boolean IOS_IS_HEADLESS();

    // ===== CẤU HÌNH TIMEOUT (GIÂY) =====
    @Key("timeout.explicit")
    @DefaultValue("10")
    long TIMEOUT_EXPLICIT();

    @Key("timeout.page.load")
    @DefaultValue("30")
    long TIMEOUT_PAGE_LOAD();

    @Key("timeout.appium.command")
    @DefaultValue("60")
    long TIMEOUT_APPIUM_COMMAND();

    @Key("timeout.device.startup")
    @DefaultValue("120")
    long TIMEOUT_DEVICE_STARTUP();

    // ===== TÍNH NĂNG BỔ TRỢ =====
    @Key("record.video.on.failure")
    @DefaultValue("true")
    boolean RECORD_VIDEO_ON_FAILURE();

    @Key("screenshot.on.failure")
    @DefaultValue("true")
    boolean SCREENSHOT_ON_FAILURE();

    @Key("screenshot.on.pass")
    @DefaultValue("true")
    boolean SCREENSHOT_ON_PASS();

    @Key("retry.count")
    @DefaultValue("1")
    int RETRY_COUNT();

    // ===== TÁC GIẢ & THÔNG TIN BÁO CÁO =====
    @Key("AUTHOR")
    @DefaultValue("QA Automation Engineer")
    String AUTHOR();

    @Key("PROJECT_NAME")
    @DefaultValue("Mobile Automation Project")
    String PROJECT_NAME();

    @Key("REPORT_TITLE")
    @DefaultValue("ExtentReport | Mobile Automation")
    String REPORT_TITLE();

    @Key("EXTENT_REPORT_NAME")
    @DefaultValue("ExtentReports")
    String EXTENT_REPORT_NAME();

    @Key("EXTENT_REPORT_FOLDER")
    @DefaultValue("exports/ExtentReports")
    String EXTENT_REPORT_FOLDER();

    @Key("OVERRIDE_REPORTS")
    @DefaultValue("yes")
    String OVERRIDE_REPORTS();
}
