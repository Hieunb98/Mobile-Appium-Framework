package com.appium.common;

import com.appium.config.ConfigFactory;
import com.appium.driver.DriverManager;
import com.appium.driver.TargetFactory;
import com.appium.base.BaseScreen;
import com.appium.utils.LogUtils;
import io.appium.java_client.AppiumDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import org.testng.annotations.Listeners;

import com.appium.projects.saucedemo.screens.CommonScreen;
import com.appium.projects.saucedemo.screens.ProductsScreen;

/**
 * BaseTest là class nền tảng cho tất cả các Test class trong Mobile Automation
 * (chuẩn CRM-frameWork).
 * Quản lý vòng đời khởi tạo Driver qua TargetFactory và dọn dẹp SoftAssert an
 * toàn trên mỗi luồng.
 * Khởi tạo sẵn CommonScreen và Entry Screen (ProductsScreen) cho các Test class
 * con kế thừa.
 */
@Listeners({ com.appium.listeners.TestListener.class })
public class BaseTest {

    protected CommonScreen commonScreen;
    protected ProductsScreen productsScreen;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        LogUtils.info("=== Nạp cấu hình hệ thống qua Owner ConfigFactory ===");
        ConfigFactory.getConfigs();
    }

    @Parameters({ "platform", "udid", "systemPort" })
    @BeforeMethod(alwaysRun = true)
    public void createDriver(
            @Optional("") String platform,
            @Optional("") String udid,
            @Optional("0") String systemPort) {

        // Dọn dẹp SoftAssert cũ của thread này trước khi chạy test mới
        BaseScreen.clearSoftAssert();

        LogUtils.info(String.format("Khởi tạo Appium Session -> Tham số XML: Platform='%s' | UDID='%s' | Port='%s'",
                platform, udid, systemPort));
        AppiumDriver driver = new TargetFactory().createInstance(platform, udid, Integer.parseInt(systemPort));
        DriverManager.setDriver(driver);

        // Khởi tạo điểm bắt đầu qua CommonScreen (BaseScreen > CommonScreen > Screen
        // con)
        commonScreen = new CommonScreen();
        productsScreen = commonScreen.getProductsScreen();
    }

    @AfterMethod(alwaysRun = true)
    public void closeDriver(ITestResult result) {
        try {
            // Kiểm tra và flush lỗi từ SoftAssert nếu có
            BaseScreen.stopSoftAssertAll();
        } catch (AssertionError ae) {
            LogUtils.error("🚨 Phát hiện Soft Assert thất bại trong test case: " + ae.getMessage());
            if (result != null) {
                result.setStatus(ITestResult.FAILURE);
                result.setThrowable(ae);
            }
        } finally {
            BaseScreen.clearSoftAssert();
            LogUtils.info("Đóng Session Appium Driver và giải phóng tài nguyên");
            DriverManager.quit();
        }
    }
}
