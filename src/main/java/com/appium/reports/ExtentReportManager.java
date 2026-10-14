package com.appium.reports;

import com.appium.constants.FrameworkConstants;
import com.appium.driver.DriverManager;
import com.appium.helpers.SystemHelpers;
import com.appium.utils.LogUtils;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.util.Objects;

/**
 * ExtentReportManager quản lý vòng đời tạo và xuất file HTML báo cáo ExtentReports (chuẩn CRM-frameWork).
 */
public final class ExtentReportManager {

    private static ExtentReports extentReports;
    private static String reportFilePath = "";

    private ExtentReportManager() {
    }

    /**
     * Khởi tạo ExtentReports và đính kèm Spark Reporter.
     */
    public static synchronized void initReports() {
        if (Objects.isNull(extentReports)) {
            extentReports = new ExtentReports();

            // Đảm bảo thư mục exports/ExtentReports đã tồn tại trên máy tính
            SystemHelpers.createFolder(FrameworkConstants.EXTENT_REPORT_FOLDER_PATH);
            reportFilePath = FrameworkConstants.EXTENT_REPORT_FILE_PATH;
            LogUtils.info("ExtentReportManager -> Khởi tạo Extent Report tại: " + reportFilePath);

            ExtentSparkReporter spark = new ExtentSparkReporter(reportFilePath);
            extentReports.attachReporter(spark);

            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle(FrameworkConstants.REPORT_TITLE);
            spark.config().setReportName(FrameworkConstants.REPORT_TITLE);

            extentReports.setSystemInfo("Framework Name", FrameworkConstants.PROJECT_NAME);
            extentReports.setSystemInfo("Author", FrameworkConstants.AUTHOR);
            extentReports.setSystemInfo("Platform", FrameworkConstants.PLATFORM.toUpperCase());
            extentReports.setSystemInfo("Target", FrameworkConstants.TARGET.toUpperCase());

            LogUtils.info("Extent Reports đã sẵn sàng ghi nhận kết quả test.");
        }
    }

    /**
     * Xuất báo cáo ra file HTML sau khi kết thúc Test Suite.
     */
    public static synchronized void flushReports() {
        if (Objects.nonNull(extentReports)) {
            extentReports.flush();
            LogUtils.info("Đã xuất file báo cáo ExtentReports thành công tại: " + reportFilePath);
        }
        ExtentTestManager.unload();
    }

    /**
     * Tạo một mục Test Case mới trong báo cáo.
     */
    public static void createTest(String testCaseName) {
        ExtentTestManager.setExtentTest(extentReports.createTest(testCaseName));
    }

    public static void createTest(String testCaseName, String description) {
        ExtentTestManager.setExtentTest(extentReports.createTest(testCaseName, description));
    }

    /**
     * Chụp ảnh màn hình thiết bị dạng Base64 và đính kèm vào ExtentReport.
     */
    public static void addScreenShot(Status status, String message) {
        if (DriverManager.getDriver() != null && ExtentTestManager.getExtentTest() != null) {
            try {
                String base64Image = "data:image/png;base64,"
                        + ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);

                ExtentTestManager.getExtentTest().log(status, message,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64Image).build());
            } catch (Exception e) {
                LogUtils.warn("Không thể đính kèm ảnh chụp màn hình vào ExtentReport: " + e.getMessage());
            }
        }
    }

    public static void addScreenShot(String message) {
        addScreenShot(Status.INFO, message);
    }

    public static void logMessage(Status status, String message) {
        if (ExtentTestManager.getExtentTest() != null) {
            ExtentTestManager.getExtentTest().log(status, message);
        }
    }

    public static void pass(String message) {
        logMessage(Status.PASS, message);
    }

    public static void fail(String message) {
        logMessage(Status.FAIL, message);
    }

    public static void skip(String message) {
        logMessage(Status.SKIP, message);
    }

    public static void info(String message) {
        logMessage(Status.INFO, message);
    }
}
