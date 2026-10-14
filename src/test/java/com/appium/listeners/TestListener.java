package com.appium.listeners;

import com.appium.constants.FrameworkConstants;
import com.appium.driver.DriverManager;
import com.appium.helpers.CaptureHelpers;
import com.appium.reports.AllureManager;
import com.appium.reports.ExtentReportManager;
import com.appium.utils.LogUtils;
import com.aventstack.extentreports.Status;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Base64;

/**
 * TestListener theo dõi vòng đời thực thi của TestNG và tự động ghi nhận báo cáo vào cả:
 * 1. Allure Report (Kèm Screenshot & Video MP4 quay màn hình).
 * 2. ExtentReports (File HTML độc lập tại exports/ExtentReports/ExtentReports.html) - chuẩn CRM-frameWork.
 */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        LogUtils.info("==================================================");
        LogUtils.info("BẮT ĐẦU CHẠY TEST SUITE: " + context.getName());
        LogUtils.info("==================================================");

        // Khởi tạo ExtentReports
        ExtentReportManager.initReports();
    }

    @Override
    public void onFinish(ITestContext context) {
        LogUtils.info("==================================================");
        LogUtils.info("KẾT THÚC TEST SUITE: " + context.getName());
        LogUtils.info("Số test PASS: " + context.getPassedTests().size() +
                ", Số test FAIL: " + context.getFailedTests().size() +
                ", Số test SKIP: " + context.getSkippedTests().size());
        LogUtils.info("==================================================");

        // Xuất file HTML báo cáo ExtentReports
        ExtentReportManager.flushReports();
    }

    @Override
    public void onTestStart(ITestResult result) {
        LogUtils.info(">>> Khởi chạy test case: " + result.getName());

        // Tạo mục kiểm thử trong ExtentReports
        String description = result.getMethod().getDescription();
        if (description != null && !description.isEmpty()) {
            ExtentReportManager.createTest(result.getName(), description);
        } else {
            ExtentReportManager.createTest(result.getName());
        }

        // Bắt đầu quay video màn hình nếu được cấu hình
        AppiumDriver driver = DriverManager.getDriver();
        if (FrameworkConstants.RECORD_VIDEO_ON_FAILURE && driver instanceof CanRecordScreen) {
            try {
                ((CanRecordScreen) driver).startRecordingScreen();
                LogUtils.info("Bắt đầu quay video màn hình cho test case: " + result.getName());
            } catch (Exception e) {
                LogUtils.warn("Không thể bắt đầu quay video: " + e.getMessage());
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogUtils.info("<<< PASSED: Test case " + result.getName() + " THÀNH CÔNG");
        ExtentReportManager.pass("Test case: " + result.getName() + " PASSED.");

        AppiumDriver driver = DriverManager.getDriver();
        if (driver instanceof CanRecordScreen) {
            try {
                ((CanRecordScreen) driver).stopRecordingScreen();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LogUtils.error("<<< FAILED: Test case " + result.getName() + " THẤT BẠI: " + result.getThrowable());

        // 1. Ghi log thất bại và đính kèm ảnh chụp màn hình vào ExtentReports
        ExtentReportManager.fail(result.getThrowable().getMessage());
        if (FrameworkConstants.SCREENSHOT_ON_FAILURE) {
            ExtentReportManager.addScreenShot(Status.FAIL, "Ảnh chụp màn hình khi thất bại");
        }

        AppiumDriver driver = DriverManager.getDriver();
        if (driver != null) {
            try {
                // 2. Chụp ảnh màn hình lưu ổ đĩa & đính kèm Allure Report
                if (FrameworkConstants.SCREENSHOT_ON_FAILURE) {
                    CaptureHelpers.captureScreenshot(result.getName());
                }

                // 3. Dừng quay video và đính kèm video MP4 vào Allure Report
                if (FrameworkConstants.RECORD_VIDEO_ON_FAILURE && driver instanceof CanRecordScreen) {
                    String base64Video = ((CanRecordScreen) driver).stopRecordingScreen();
                    byte[] videoBytes = Base64.getDecoder().decode(base64Video);
                    AllureManager.saveVideoMP4(result.getName(), videoBytes);
                    LogUtils.info("Đã đính kèm video MP4 màn hình vào Allure");
                }
            } catch (Exception e) {
                LogUtils.warn("Lỗi khi lưu bằng chứng (screenshot/video): " + e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LogUtils.warn("<<< SKIPPED: Bỏ qua test case: " + result.getName());
        ExtentReportManager.skip("Test case: " + result.getName() + " SKIPPED.");
    }
}
