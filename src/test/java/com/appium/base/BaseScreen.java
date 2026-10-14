package com.appium.base;

import com.appium.constants.FrameworkConstants;
import com.appium.driver.DriverManager;
import com.appium.enums.FailureHandling;
import com.appium.reports.AllureManager;
import com.appium.utils.LogUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * BaseScreen — Core Engine nền tảng cho toàn bộ Screen Objects trong Mobile
 * Automation.
 * <p>
 * Đồng bộ chuẩn kiến trúc BasePage từ CRM-frameWork:
 * 1. Độc lập 100%, không phụ thuộc vào thư viện keyword bên ngoài.
 * 2. Kế thừa OOP: Các Screen class mở rộng BaseScreen và gọi trực tiếp các thao
 * tác cơ bản của Mobile UI.
 * 3. Lazy Driver Pattern: Luôn lấy {@code DriverManager.getDriver()} tại thời
 * điểm chạy.
 * 4. Thread-Safe SoftAssert: Hỗ trợ gom lỗi mượt mà qua
 * {@code ThreadLocal<SoftAssert>}.
 * 5. Tự động ghi Log (LogUtils), ExtentReport và Allure Step cho từng hành
 * động.
 */
public abstract class BaseScreen {

    // =========================================================================
    // 1. THREAD-SAFE SOFT ASSERT
    // =========================================================================

    private static final ThreadLocal<SoftAssert> softAssertHolder = new ThreadLocal<>();

    /**
     * Lấy SoftAssert của thread hiện tại. Tự động khởi tạo nếu chưa có.
     */
    protected SoftAssert softAssert() {
        if (softAssertHolder.get() == null) {
            softAssertHolder.set(new SoftAssert());
        }
        return softAssertHolder.get();
    }

    /**
     * Thu thập và xác nhận toàn bộ SoftAssert ở cuối test method.
     */
    public static void stopSoftAssertAll() {
        if (softAssertHolder.get() != null) {
            try {
                softAssertHolder.get().assertAll();
            } finally {
                softAssertHolder.remove();
            }
        }
    }

    /**
     * Dọn dẹp SoftAssert cho thread hiện tại (thường gọi ở @BeforeMethod).
     */
    public static void clearSoftAssert() {
        softAssertHolder.remove();
    }

    // =========================================================================
    // 2. INTERNAL DRIVER & WAIT HELPERS
    // =========================================================================

    protected AppiumDriver getDriver() {
        AppiumDriver driver = DriverManager.getDriver();
        if (driver == null) {
            throw new IllegalStateException(
                    "AppiumDriver chưa được khởi tạo. Vui lòng kiểm tra TargetFactory / DriverManager.");
        }
        return driver;
    }

    protected WebDriverWait getWait(long timeoutSeconds) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
    }

    protected WebDriverWait getWait() {
        return getWait(FrameworkConstants.EXPLICIT_TIMEOUT);
    }

    // =========================================================================
    // CROSS-PLATFORM LOCATOR HELPERS
    // =========================================================================

    /**
     * Kiểm tra xem session hiện tại có phải đang chạy trên Android hay không.
     */
    protected boolean isAndroid() {
        return DriverManager.getDriver() instanceof AndroidDriver;
    }

    /**
     * Kiểm tra xem session hiện tại có phải đang chạy trên iOS hay không.
     */
    protected boolean isIOS() {
        return DriverManager.getDriver() instanceof IOSDriver;
    }

    /**
     * Bộ chọn Locator đa nền tảng linh hoạt (Cross-Platform Dynamic Locator):
     * - Nếu session hiện tại là Android: tự động trả về androidBy.
     * - Nếu session hiện tại là iOS: tự động trả về iosBy.
     * Giúp Screen Object dùng chung 100% method mà không cần tách riêng class.
     *
     * @param androidBy Locator định nghĩa cho ứng dụng Android
     * @param iosBy     Locator định nghĩa cho ứng dụng iOS
     * @return By locator tương ứng với OS đang thực thi
     */
    protected By loc(By androidBy, By iosBy) {
        return isAndroid() ? androidBy : iosBy;
    }

    // =========================================================================
    // 3. WAIT METHODS
    // =========================================================================

    protected WebElement waitForElementVisible(By by) {
        return waitForElementVisible(by, FrameworkConstants.EXPLICIT_TIMEOUT);
    }

    protected WebElement waitForElementVisible(By by, long timeoutSeconds) {
        return getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected WebElement waitForElementPresent(By by) {
        return waitForElementPresent(by, FrameworkConstants.EXPLICIT_TIMEOUT);
    }

    protected WebElement waitForElementPresent(By by, long timeoutSeconds) {
        return getWait(timeoutSeconds).until(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected WebElement waitForElementClickable(By by) {
        return waitForElementClickable(by, FrameworkConstants.EXPLICIT_TIMEOUT);
    }

    protected WebElement waitForElementClickable(By by, long timeoutSeconds) {
        return getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(by));
    }

    protected boolean waitForElementInvisible(By by) {
        return waitForElementInvisible(by, FrameworkConstants.EXPLICIT_TIMEOUT);
    }

    protected boolean waitForElementInvisible(By by, long timeoutSeconds) {
        return getWait(timeoutSeconds).until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    // =========================================================================
    // 4. INTERACTION METHODS
    // =========================================================================

    @Step("Click vào element: {by}")
    protected void click(By by) {
        LogUtils.info("Click element: " + by);
        try {
            waitForElementClickable(by).click();
        } catch (Throwable e) {
            LogUtils.error("Không thể click vào element: " + by);
            throw e;
        }
    }

    @Step("Nhập text '{text}' vào element: {by}")
    protected void setText(By by, String text) {
        LogUtils.info("Nhập văn bản '" + text + "' vào element: " + by);
        WebElement element = waitForElementVisible(by);
        element.click();
        try {
            element.clear();
        } catch (Exception ignored) {
        }
        element.sendKeys(text);
    }

    @Step("Xóa nội dung của element: {by}")
    protected void clearText(By by) {
        LogUtils.info("Xóa nội dung element: " + by);
        WebElement element = waitForElementVisible(by);
        element.clear();
        // Fallback xử lý cho iOS khi element.clear() không làm sạch dữ liệu cũ
        if (isIOS()) {
            String currentText = element.getText();
            if (currentText != null && !currentText.isEmpty()) {
                StringBuilder backspaces = new StringBuilder();
                for (int i = 0; i < currentText.length(); i++) {
                    backspaces.append("\b");
                }
                element.sendKeys(backspaces.toString());
            }
        }
    }

    @Step("Xóa và nhập text '{text}' vào element: {by}")
    protected void clearAndFillText(By by, String text) {
        clearText(by);
        setText(by, text);
    }

    // =========================================================================
    // 5. READ METHODS
    // =========================================================================

    @Step("Lấy text từ element: {by}")
    protected String getText(By by) {
        String text = waitForElementVisible(by).getText();
        LogUtils.info("Lấy text từ element " + by + " -> '" + text + "'");
        return text;
    }

    protected String getAttribute(By by, String attributeName) {
        return waitForElementVisible(by).getAttribute(attributeName);
    }

    protected boolean isElementDisplayed(By by) {
        return isElementDisplayed(by, FrameworkConstants.EXPLICIT_TIMEOUT);
    }

    protected boolean isElementDisplayed(By by, long timeoutSeconds) {
        try {
            return waitForElementVisible(by, timeoutSeconds).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean checkElementExist(By by) {
        return !getDriver().findElements(by).isEmpty();
    }

    // =========================================================================
    // 7. VERIFY & ASSERTION ENGINE (Soft & Hard Assert)
    // =========================================================================

    @Step("Verify điều kiện: {message}")
    protected boolean verifyTrue(boolean condition, String message) {
        return verifyTrue(condition, message, FailureHandling.CONTINUE_ON_FAILURE);
    }

    @Step("Verify điều kiện: {message}")
    protected boolean verifyTrue(boolean condition, String message, FailureHandling flowControl) {
        if (flowControl == FailureHandling.CONTINUE_ON_FAILURE) {
            softAssert().assertTrue(condition, message);
        } else if (flowControl == FailureHandling.STOP_ON_FAILURE) {
            Assert.assertTrue(condition, message);
        }
        return condition;
    }

    @Step("Verify bằng nhau: actual='{actual}' expected='{expected}'")
    protected boolean verifyEquals(Object actual, Object expected, String message) {
        boolean result = actual != null && actual.equals(expected);
        AllureManager.saveTextLog("Verify Equals",
                message + " | actual=[" + actual + "] expected=[" + expected + "] -> " + result);
        softAssert().assertEquals(actual, expected, message);
        return result;
    }

    @Step("Verify chứa: actual='{actual}' expected='{expected}'")
    protected boolean verifyContains(String actual, String expected, String message) {
        boolean result = actual != null && actual.contains(expected);
        AllureManager.saveTextLog("Verify Contains",
                message + " | actual=[" + actual + "] expected=[" + expected + "] -> " + result);
        softAssert().assertTrue(result, message + " | actual=[" + actual + "] expected to contain=[" + expected + "]");
        return result;
    }

    @Step("Verify element hiển thị: {by}")
    protected boolean verifyElementDisplayed(By by, String message) {
        boolean displayed = isElementDisplayed(by);
        softAssert().assertTrue(displayed, message);
        return displayed;
    }

    // =========================================================================
    // 8. MOBILE GESTURES & HARDWARE KEYS
    // =========================================================================

    @Step("Ẩn bàn phím ảo")
    protected void hideKeyboard() {
        try {
            if (isAndroid()) {
                if (getDriver() instanceof HidesKeyboard) {
                    ((HidesKeyboard) getDriver()).hideKeyboard();
                    LogUtils.info("Đã ẩn bàn phím ảo (Android)");
                }
            } else {
                // iOS: Gọi hideKeyboard() mặc định
                try {
                    if (getDriver() instanceof HidesKeyboard) {
                        ((HidesKeyboard) getDriver()).hideKeyboard();
                        LogUtils.info("Đã ẩn bàn phím ảo (iOS)");
                    }
                } catch (Exception e) {
                    // Fallback cho iOS: Bấm nút 'Done', 'return', 'Nhập', 'go', 'Search' hoặc tap
                    // ngoài màn hình
                    By doneBtn = AppiumBy.xpath(
                            "//XCUIElementTypeButton[@label='Done' or @label='return' or @label='Hide keyboard' or @label='Nhập' or @label='Search' or @label='go']");
                    if (checkElementExist(doneBtn)) {
                        click(doneBtn);
                        LogUtils.info("Đã ẩn bàn phím iOS bằng nút " + doneBtn);
                    }
                }
            }
        } catch (Exception ignored) {
            // Bàn phím không hiển thị hoặc đã ẩn, an toàn bỏ qua
        }
    }

    @Step("Nhấn phím Back")
    protected void pressBack() {
        if (isAndroid()) {
            LogUtils.info("Bấm phím Back phần cứng (Android)");
            ((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            LogUtils.info("Gọi điều hướng Back (iOS)");
            try {
                getDriver().navigate().back();
            } catch (Exception e) {
                // Fallback nếu navigate().back() không tác dụng: tìm nút back ở NavigationBar
                By iosBackBtn = AppiumBy.iOSClassChain("**/XCUIElementTypeNavigationBar/XCUIElementTypeButton[1]");
                if (checkElementExist(iosBackBtn)) {
                    click(iosBackBtn);
                }
            }
        }
    }

    @Step("Thực hiện Gesture Swipe từ ({from.x}, {from.y}) tới ({to.x}, {to.y})")
    protected void swipe(Point from, Point to, int durationMs) {
        LogUtils.info("Thực hiện Swipe: (" + from.x + ", " + from.y + ") -> (" + to.x + ", " + to.y + ")");
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), from.x, from.y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(durationMs), PointerInput.Origin.viewport(), to.x,
                        to.y))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        getDriver().perform(Collections.singletonList(swipe));
    }

    @Step("Cuộn màn hình lên (Swipe Up)")
    protected void swipeUp() {
        Dimension size = getDriver().manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);
        swipe(new Point(startX, startY), new Point(startX, endY), 600);
    }

    @Step("Cuộn màn hình xuống (Swipe Down)")
    protected void swipeDown() {
        Dimension size = getDriver().manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.2);
        int endY = (int) (size.height * 0.8);
        swipe(new Point(startX, startY), new Point(startX, endY), 600);
    }

    @Step("Cuộn tìm text: '{text}'")
    protected WebElement scrollToText(String text) {
        LogUtils.info("Cuộn tới khi tìm thấy text: " + text);
        if (isAndroid()) {
            return getDriver().findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"" + text
                            + "\"))"));
        } else {
            // iOS: Dùng native mobile command 'mobile: scroll' của Apple XCUITest
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("predicateString", "label CONTAINS '" + text + "' OR name CONTAINS '" + text + "'");
                params.put("toVisible", true);
                getDriver().executeScript("mobile: scroll", params);
            } catch (Exception e) {
                LogUtils.warn("mobile: scroll không hoàn tất ngay, kiểm tra element: " + e.getMessage());
            }
            By iosElement = AppiumBy.iOSClassChain(
                    "**/XCUIElementTypeAny[`label CONTAINS '" + text + "' OR name CONTAINS '" + text + "'`][1]");
            return waitForElementVisible(iosElement);
        }
    }

    // =========================================================================
    // 9. NATIVE ALERTS & SYSTEM DIALOGS
    // =========================================================================

    @Step("Chấp nhận Alert / Dialog hệ thống (Accept)")
    protected void acceptAlert() {
        try {
            getWait(5).until(ExpectedConditions.alertIsPresent());
            getDriver().switchTo().alert().accept();
            LogUtils.info("Đã chấp nhận Alert");
        } catch (Exception e) {
            LogUtils.warn("Không tìm thấy Alert để chấp nhận: " + e.getMessage());
        }
    }

    @Step("Từ chối / Đóng Alert (Dismiss)")
    protected void dismissAlert() {
        try {
            getWait(5).until(ExpectedConditions.alertIsPresent());
            getDriver().switchTo().alert().dismiss();
            LogUtils.info("Đã từ chối Alert");
        } catch (Exception e) {
            LogUtils.warn("Không tìm thấy Alert để từ chối: " + e.getMessage());
        }
    }

    @Step("Lấy nội dung Alert text")
    protected String getAlertText() {
        try {
            getWait(5).until(ExpectedConditions.alertIsPresent());
            return getDriver().switchTo().alert().getText();
        } catch (Exception e) {
            LogUtils.warn("Không có Alert hiển thị: " + e.getMessage());
            return "";
        }
    }
}
