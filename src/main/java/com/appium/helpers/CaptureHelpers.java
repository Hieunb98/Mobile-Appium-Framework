package com.appium.helpers;

import com.appium.constants.FrameworkConstants;
import com.appium.driver.DriverManager;
import com.appium.reports.AllureManager;
import com.appium.utils.LogUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CaptureHelpers hỗ trợ chụp ảnh màn hình lưu vào ổ đĩa và đính kèm Allure Report (chuẩn CRM-frameWork).
 */
public final class CaptureHelpers {

    private CaptureHelpers() {
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");

    /**
     * Chụp ảnh màn hình hiện tại và lưu vào thư mục exports/ExportData/Images.
     * @param screenshotName Tên gợi nhớ cho ảnh chụp
     */
    public static void captureScreenshot(String screenshotName) {
        if (DriverManager.getDriver() == null) {
            LogUtils.warn("Không thể chụp màn hình do AppiumDriver chưa được khởi tạo!");
            return;
        }

        try {
            TakesScreenshot ts = (TakesScreenshot) DriverManager.getDriver();
            File source = ts.getScreenshotAs(OutputType.FILE);
            byte[] screenshotBytes = ts.getScreenshotAs(OutputType.BYTES);

            // Đính kèm trực tiếp vào Allure Report bằng Allure API (đảm bảo 100% hiển thị)
            io.qameta.allure.Allure.addAttachment(screenshotName, "image/png", new java.io.ByteArrayInputStream(screenshotBytes), ".png");
            AllureManager.saveScreenshotPNG(screenshotName, screenshotBytes);

            // Lưu file vật lý vào thư mục exports
            String folderPath = FrameworkConstants.EXPORT_CAPTURE_PATH;
            SystemHelpers.createFolder(folderPath);

            String fileName = screenshotName + "_" + DATE_FORMAT.format(new Date()) + ".png";
            File destination = new File(folderPath + File.separator + fileName);
            FileUtils.copyFile(source, destination);

            LogUtils.info("Đã lưu ảnh chụp màn hình tại: " + destination.getAbsolutePath());
        } catch (IOException e) {
            LogUtils.error("Lỗi khi lưu ảnh chụp màn hình: " + e.getMessage(), e);
        } catch (Exception e) {
            LogUtils.error("Không thể chụp màn hình thiết bị: " + e.getMessage(), e);
        }
    }
}
