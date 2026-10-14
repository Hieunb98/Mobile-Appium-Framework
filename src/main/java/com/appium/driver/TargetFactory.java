package com.appium.driver;

import com.appium.constants.FrameworkConstants;
import com.appium.enums.Target;
import com.appium.exceptions.TargetNotValidException;
import com.appium.utils.LogUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.Capabilities;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * TargetFactory quyết định môi trường chạy (Local hay Remote/Grid/Cloud Farm).
 * Cấu trúc hoàn toàn đối xứng và chuẩn hóa theo TargetFactory của CRM-frameWork:
 * 1. createInstance(): Nạp mặc định theo PLATFORM (android_emulator, android_real, ios_simulator, ios_real) từ config.properties.
 * 2. createInstance(platform, udid, systemPort): Đa hình nạp chồng (Overloading) có xử lý thứ tự ưu tiên (CLI > XML > Config).
 * 3. createRemoteInstance(platform, capability): Nhận Capabilities truyền thẳng vào AppiumDriver giống hệt CRM.
 */
public class TargetFactory {

    /**
     * 1. Khởi tạo AppiumDriver dựa vào cấu hình mặc định trong config.properties (chuẩn CRM).
     */
    public AppiumDriver createInstance() {
        Target target = Target.valueOf(FrameworkConstants.TARGET.toUpperCase());
        AppiumDriver driver;

        String platform = FrameworkConstants.PLATFORM;
        String udid = getDefaultUdid(platform);
        int port = getDefaultPort(platform);
        DeviceFactory deviceFactory = resolveDeviceFactory(platform);

        switch (target) {
            case LOCAL:
                LogUtils.info(String.format("TargetFactory -> Chế độ chạy LOCAL mặc định: Platform=%s | UDID=%s | Port=%d", platform, udid, port));
                driver = deviceFactory.createDriver(udid, port);
                break;

            case REMOTE:
                LogUtils.info(String.format("TargetFactory -> Chế độ chạy REMOTE mặc định: Platform=%s | UDID=%s | Port=%d", platform, udid, port));
                driver = createRemoteInstance(platform, deviceFactory.getOptions(udid, port));
                break;

            default:
                throw new TargetNotValidException(target.toString());
        }
        return driver;
    }

    /**
     * 2. Khởi tạo AppiumDriver với tham số truyền vào từ file TestNG XML hoặc tham số dòng lệnh Maven.
     * Xử lý thứ tự ưu tiên: CLI > XML Parameter > Config file (chuẩn CRM).
     */
    public AppiumDriver createInstance(String platform, String udid, int systemPort) {
        Target target = Target.valueOf(FrameworkConstants.TARGET.toUpperCase());
        AppiumDriver driver;

        // 1. Thứ tự ưu tiên Platform
        String mavenPlatform = System.getProperty("platform");
        String targetPlatform = (mavenPlatform != null && !mavenPlatform.trim().isEmpty())
                ? mavenPlatform.trim()
                : (platform != null && !platform.trim().isEmpty())
                        ? platform.trim()
                        : FrameworkConstants.PLATFORM;

        boolean isIOS = isIosPlatform(targetPlatform);

        // 2. Thứ tự ưu tiên UDID
        String cliUdid = System.getProperty("udid");
        if (cliUdid == null || cliUdid.trim().isEmpty()) {
            cliUdid = isIOS ? System.getProperty("ios.udid") : System.getProperty("android.udid");
        }

        String defaultUdid = getDefaultUdid(targetPlatform);
        String targetUdid = (cliUdid != null && !cliUdid.trim().isEmpty())
                ? cliUdid.trim()
                : (udid != null && !udid.trim().isEmpty())
                        ? udid.trim()
                        : defaultUdid;

        // 3. Thứ tự ưu tiên Port
        String cliPort = System.getProperty("port");
        if (cliPort == null || cliPort.trim().isEmpty()) {
            cliPort = isIOS ? System.getProperty("ios.wdaLocalPort") : System.getProperty("android.systemPort");
        }

        int defaultPort = getDefaultPort(targetPlatform);
        int targetPort = (cliPort != null && !cliPort.trim().isEmpty())
                ? Integer.parseInt(cliPort.trim())
                : (systemPort > 0 ? systemPort : defaultPort);

        Allure.step(String.format("🤖 Run on mobile: Platform=%s | UDID=%s | Port=%d | Target=%s",
                targetPlatform, targetUdid, targetPort, target));

        DeviceFactory deviceFactory = resolveDeviceFactory(targetPlatform);

        switch (target) {
            case LOCAL:
                LogUtils.info(String.format("TargetFactory -> LOCAL: Khởi tạo %s trên thiết bị %s (Port: %d)", targetPlatform, targetUdid, targetPort));
                driver = deviceFactory.createDriver(targetUdid, targetPort);
                break;

            case REMOTE:
                LogUtils.info(String.format("TargetFactory -> REMOTE: Kết nối tới Grid Hub để chạy %s trên thiết bị %s", targetPlatform, targetUdid));
                driver = createRemoteInstance(targetPlatform, deviceFactory.getOptions(targetUdid, targetPort));
                break;

            default:
                throw new TargetNotValidException(target.toString());
        }
        return driver;
    }

    /**
     * 3. Phương thức riêng biệt cấu hình kết nối tới Remote Server / Selenium Grid Hub (chuẩn CRM).
     * Nhận trực tiếp Capabilities và truyền vào Driver mà không cần ép kiểu.
     */
    private AppiumDriver createRemoteInstance(String platform, Capabilities capability) {
        AppiumDriver remoteDriver = null;
        try {
            String gridURL = String.format("http://%s:%s", FrameworkConstants.REMOTE_URL, FrameworkConstants.REMOTE_PORT);
            LogUtils.info("TargetFactory -> Đang kết nối tới Remote Grid URL: " + gridURL);

            URL remoteServerUrl = new URL(gridURL);

            if (isIosPlatform(platform)) {
                remoteDriver = new IOSDriver(remoteServerUrl, capability);
            } else {
                remoteDriver = new AndroidDriver(remoteServerUrl, capability);
            }
        } catch (MalformedURLException e) {
            LogUtils.error("Grid URL không hợp lệ hoặc Remote Port không khả dụng: " + FrameworkConstants.REMOTE_URL, e);
        } catch (IllegalArgumentException e) {
            LogUtils.error(String.format("Platform %s không được hỗ trợ trên Remote Grid", platform), e);
        }
        return remoteDriver;
    }

    /**
     * Phân giải DeviceFactory linh hoạt theo tên platform
     */
    public DeviceFactory resolveDeviceFactory(String platform) {
        String p = (platform != null && !platform.trim().isEmpty()) ? platform : FrameworkConstants.PLATFORM;
        return DeviceFactory.fromPlatform(p);
    }

    private boolean isIosPlatform(String platform) {
        if (platform == null) return false;
        return platform.trim().toLowerCase().startsWith("ios");
    }

    private String getDefaultUdid(String platform) {
        if (platform == null) return FrameworkConstants.ANDROID_UDID;
        String p = platform.trim().toLowerCase();

        switch (p) {
            case "android_real":
                return !FrameworkConstants.ANDROID_REAL_UDID.isEmpty()
                        ? FrameworkConstants.ANDROID_REAL_UDID
                        : FrameworkConstants.ANDROID_UDID;
            case "android_emulator":
            case "android":
                return !FrameworkConstants.ANDROID_EMULATOR_UDID.isEmpty()
                        ? FrameworkConstants.ANDROID_EMULATOR_UDID
                        : FrameworkConstants.ANDROID_UDID;
            case "ios_real":
                return !FrameworkConstants.IOS_REAL_UDID.isEmpty()
                        ? FrameworkConstants.IOS_REAL_UDID
                        : FrameworkConstants.IOS_UDID;
            case "ios_simulator":
            case "ios_sim":
            case "ios":
                return !FrameworkConstants.IOS_SIM_UDID.isEmpty()
                        ? FrameworkConstants.IOS_SIM_UDID
                        : FrameworkConstants.IOS_UDID;
            default:
                return isIosPlatform(platform) ? FrameworkConstants.IOS_UDID : FrameworkConstants.ANDROID_UDID;
        }
    }

    private int getDefaultPort(String platform) {
        return isIosPlatform(platform) ? FrameworkConstants.IOS_WDA_LOCAL_PORT : FrameworkConstants.ANDROID_SYSTEM_PORT;
    }
}
