package com.appium.listeners;

import com.appium.constants.FrameworkConstants;
import com.appium.driver.DriverManager;
import com.appium.utils.LogUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;

/**
 * AllureListener gắn trực tiếp vào Allure TestLifecycleListener SPI (chuẩn CRM-frameWork).
 * Tự động chụp và đính kèm ảnh bằng chứng trực tiếp vào Allure Report ngay trước khi test case kết thúc (beforeTestStop).
 */
public class AllureListener implements TestLifecycleListener {

    @Override
    public void beforeTestSchedule(TestResult result) {
    }

    @Override
    public void afterTestSchedule(TestResult result) {
    }

    @Override
    public void beforeTestUpdate(TestResult result) {
    }

    @Override
    public void afterTestUpdate(TestResult result) {
    }

    @Override
    public void beforeTestStart(TestResult result) {
    }

    @Override
    public void afterTestStart(TestResult result) {
    }

    @Override
    public void beforeTestStop(TestResult result) {
        if (DriverManager.getDriver() != null) {
            try {
                // 1. Chụp ảnh bằng chứng khi Test PASS
                if (FrameworkConstants.SCREENSHOT_ON_PASS && Status.PASSED.equals(result.getStatus())) {
                    byte[] bytes = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
                    Allure.addAttachment(result.getName() + " [Passed Evidence]", "image/png", new ByteArrayInputStream(bytes), ".png");
                    LogUtils.info("AllureListener -> Đã đính kèm ảnh PASSED vào Allure: " + result.getName());
                }

                // 2. Chụp ảnh bằng chứng khi Test FAIL
                if (FrameworkConstants.SCREENSHOT_ON_FAILURE && (Status.FAILED.equals(result.getStatus()) || Status.BROKEN.equals(result.getStatus()))) {
                    byte[] bytes = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
                    Allure.addAttachment(result.getName() + " [Failure Evidence]", "image/png", new ByteArrayInputStream(bytes), ".png");
                    LogUtils.info("AllureListener -> Đã đính kèm ảnh FAILED vào Allure: " + result.getName());
                }
            } catch (Exception e) {
                LogUtils.warn("AllureListener -> Lỗi khi chụp ảnh đính kèm Allure: " + e.getMessage());
            }
        }
    }

    @Override
    public void afterTestStop(TestResult result) {
    }

    @Override
    public void beforeTestWrite(TestResult result) {
    }

    @Override
    public void afterTestWrite(TestResult result) {
    }
}
