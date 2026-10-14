package com.appium.projects.saucedemo.testcases;

import com.appium.common.BaseTest;
import com.appium.driver.DriverManager;
import com.appium.projects.saucedemo.screens.LoginScreen;
import com.appium.projects.saucedemo.screens.ProductsScreen;
import com.appium.utils.LogUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * MobileInteractionsPracticeTest — BỘ BÀI TẬP THỰC CHIẾN TƯƠNG TÁC MOBILE VỚI
 * APPIUM
 * ==============================================================================
 * Khác với bài tập thuật toán lý thuyết đơn thuần, file này chạy trực tiếp với
 * Appium Driver thật
 * trên ứng dụng SauceLabs My Demo App (chạy được trên cả Android Emulator và
 * iOS Simulator).
 * 
 * HƯỚNG DẪN LUYỆN TẬP:
 * - Đọc kỹ yêu cầu đề bài và các bước TODO trong từng @Test method.
 * - Tự viết code thao tác vào vùng trống bên dưới các bước TODO.
 * - Bấm nút Run (mũi tên xanh) để chạy trực tiếp trên thiết bị ảo xem màn hình
 * phản hồi.
 * - ĐÁP ÁN MẪU ĐƯỢC ĐẶT Ở CUỐI FILE trong class SolutionReference (chỉ tham
 * khảo khi cần đối chiếu).
 */
@Epic("Appium Mobile Automation Practice")
@Feature("Real Device & Simulator Mobile Interactions")
public class MobileInteractionsPracticeTest extends BaseTest {

    // ==========================================================================
    // BÀI TẬP 1: NHẬP LIỆU & XỬ LÝ ẨN BÀN PHÍM ĐA NỀN TẢNG (HIDE KEYBOARD)
    // ==========================================================================
    /**
     * ĐỀ BÀI 1:
     * 1. Mở menu và chuyển đến màn hình Đăng nhập (LoginScreen).
     * 2. Định vị ô input Username (Lưu ý: hỗ trợ cả Android và iOS).
     * 3. Nhập text "standard_user" vào ô Username.
     * 4. Thực hiện ẩn bàn phím ảo theo cơ chế riêng của từng hệ điều hành:
     * - Nếu là Android: Dùng API HidesKeyboard.
     * - Nếu là iOS: Gửi phím Return ("\n") hoặc tìm nút "Done".
     * 5. Lấy giá trị thực tế của ô Username ("text" trên Android hoặc "value" trên
     * iOS)
     * và assert bằng với "standard_user".
     */
    @Test(priority = 1, description = "Bài tập 1: Thực hành nhập liệu và ẩn bàn phím an toàn cho cả Android & iOS")
    @Story("Bàn phím & Form Input")
    public void practice01_InputAndHideKeyboard() {
        LogUtils.info(">>> BẮT ĐẦU BÀI TẬP 1: NHẬP LIỆU & ẨN BÀN PHÍM <<<");
        ProductsScreen productsScreen = new ProductsScreen();
        LoginScreen loginScreen = productsScreen.openMenuAndGoToLogin();

        AppiumDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // TODO BƯỚC 1: Khai báo By txtUsername dùng linh hoạt cho cả Android và iOS
        // Gợi ý: (driver instanceof AndroidDriver) ? AppiumBy.accessibilityId(...) :
        // AppiumBy.iOSClassChain(...)
        By userNameInput = (driver instanceof AndroidDriver) ? AppiumBy.accessibilityId("test-Username")
                : AppiumBy.accessibilityId("Username");

        // TODO BƯỚC 2: Chờ ô input hiển thị, clear nội dung cũ và
        // sendKeys("standard_user")
        WebElement username = wait.until(ExpectedConditions.presenceOfElementLocated(userNameInput));
        username.clear();
        username.sendKeys("standard_user");
        // TODO BƯỚC 3: Xử lý ẩn bàn phím dựa trên hệ điều hành đang chạy (isAndroid vs
        // iOS)
        if (driver instanceof HidesKeyboard) {
            ((HidesKeyboard) driver).hideKeyboard();
        }
        // TODO BƯỚC 4: Lấy giá trị actualValue từ element và Assert.assertEquals với
        // "standard_user"

    }

    // ==========================================================================
    // BÀI TẬP 2: CỬ CHỈ VUỐT NGÓN TAY CHUẨN W3C TOUCH GESTURE (SWIPE UP / DOWN)
    // ==========================================================================
    /**
     * ĐỀ BÀI 2:
     * 1. Lấy kích thước màn hình thiết bị hiện tại (Dimension size).
     * 2. Tính toán tọa độ Responsive để vuốt từ dưới lên (Swipe Up để xem nội dung
     * bên dưới):
     * - from: X = width / 2, Y = 75% chiều cao (height * 0.75).
     * - to: X = width / 2, Y = 25% chiều cao (height * 0.25).
     * 3. Tạo PointerInput kiểu TOUCH có tên "finger1".
     * 4. Tạo Sequence và thêm lần lượt 4 bước:
     * - Di chuyển đến tọa độ 'from' (PointerMove).
     * - Chạm ngón tay xuống (PointerDown).
     * - Kéo ngón tay đến tọa độ 'to' trong 600ms (PointerMove).
     * - Nhấc ngón tay lên (PointerUp).
     * 5. Thực thi cử chỉ vuốt qua driver.perform(...) và kiểm tra màn hình phản hồi
     * tốt.
     */
    @Test(priority = 2, description = "Bài tập 2: Tự tính tọa độ và vuốt cuộn màn hình bằng W3C Pointer Actions")
    @Story("W3C Touch Gestures")
    public void practice02_W3CSwipeGesture() {
        LogUtils.info(">>> BẮT ĐẦU BÀI TẬP 2: VUỐT MÀN HÌNH BẰNG W3C TOUCH ACTION <<<");

        AppiumDriver driver = DriverManager.getDriver();

        // TODO BƯỚC 1: Lấy width và height của màn hình hiện tại

        // TODO BƯỚC 2: Tính toán 2 điểm Point from và Point to theo tỷ lệ 75% và 25%

        // TODO BƯỚC 3: Tạo đối tượng PointerInput finger và Sequence swipe

        // TODO BƯỚC 4: Gắn 4 hành động (Move -> Down -> Move -> Up) vào Sequence

        // TODO BƯỚC 5: Gọi driver.perform(...) để thực thi cử chỉ vuốt ngón tay thật

    }

    // ==========================================================================
    // BÀI TẬP 3: CUỘN TÌM PHẦN TỬ THEO TEXT (ANDROID UISCROLLABLE VS IOS SCRIPT)
    // ==========================================================================
    /**
     * ĐỀ BÀI 3:
     * Tìm sản phẩm "Sauce Labs Onesie" ở tít dưới đáy danh sách sản phẩm:
     * 1. Nếu đang chạy trên Android:
     * - Sử dụng cú pháp native UiScrollable của Google Android UiAutomator:
     * "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new
     * UiSelector().text(\"Sauce Labs Onesie\"))"
     * 2. Nếu đang chạy trên iOS:
     * - Vì Apple không có UiScrollable, gọi script native của Apple:
     * driver.executeScript("mobile: scroll", Map.of("direction", "down"))
     * - Sau đó tìm element bằng iOSNsPredicateString theo label hoặc name.
     * 3. Assert phần tử tìm được hiển thị trên màn hình (isDisplayed() == true).
     */
    @Test(priority = 3, description = "Bài tập 3: Cuộn trang tìm kiếm sản phẩm 'Sauce Labs Onesie' ở cuối màn hình")
    @Story("Cuộn tìm kiếm Native (Scroll into view)")
    public void practice03_ScrollToElementDifference() {
        LogUtils.info(">>> BẮT ĐẦU BÀI TẬP 3: CUỘN TÌM KIẾM ELEMENT ĐẶC THÙ HỆ ĐIỀU HÀNH <<<");

        AppiumDriver driver = DriverManager.getDriver();
        String targetText = "Sauce Labs Onesie";
        WebElement targetElement = null;

        // TODO BƯỚC 1: Viết nhánh xử lý nếu (driver instanceof AndroidDriver) dùng
        // UiScrollable

        // TODO BƯỚC 2: Viết nhánh xử lý nếu là iOS dùng executeScript("mobile: scroll",
        // ...)

        // TODO BƯỚC 3: Assert targetElement.isDisplayed() == true

    }

    // ==========================================================================
    // BÀI TẬP 4: XỬ LÝ PHÍM BACK (ANDROID HARDWARE KEY VS IOS NAVIGATION BACK)
    // ==========================================================================
    /**
     * ĐỀ BÀI 4:
     * 1. Mở màn hình Login.
     * 2. Chờ màn hình Login hiển thị thành công.
     * 3. Thực hiện quay lại (BACK) màn hình chính:
     * - Trên Android: Bấm phím cứng vật lý BACK qua ((AndroidDriver)
     * driver).pressKey(new KeyEvent(AndroidKey.BACK)).
     * - Trên iOS: Vì Apple không có phím cứng Back, mở lại Menu và chọn "menu item
     * catalog" (hoặc tìm nút Back nếu có).
     * 4. Verify rằng bạn đã quay trở về màn hình danh sách Products.
     */
    @Test(priority = 4, description = "Bài tập 4: Điều hướng vào màn hình trong và thực hiện Back về màn hình chính")
    @Story("Navigation & Hardware Back Key")
    public void practice04_HardwareBackVsNavigationBack() {
        LogUtils.info(">>> BẮT ĐẦU BÀI TẬP 4: QUAY LẠI MÀN HÌNH TRƯỚC (BACK ACTION) <<<");

        ProductsScreen productsScreen = new ProductsScreen();
        productsScreen.openMenuAndGoToLogin();

        AppiumDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // TODO BƯỚC 1: Chờ tiêu đề màn hình Login hiển thị

        // TODO BƯỚC 2: Xử lý Back theo hệ điều hành (Android bấm phím cứng, iOS bấm
        // điều hướng)

        // TODO BƯỚC 3: Kiểm tra đã quay về màn hình Products thành công

    }

    // ==========================================================================
    // BÀI TẬP 5: QUẢN LÝ ỨNG DỤNG CHẠY NỀN (BACKGROUND & RESUME APP)
    // ==========================================================================
    /**
     * ĐỀ BÀI 5:
     * 1. Kiểm tra ứng dụng đang hiển thị bình thường ở màn hình Products.
     * 2. Sử dụng interface InteractsWithApps của Appium để đưa App xuống chạy nền
     * (Background) trong 3 giây:
     * ((InteractsWithApps) driver).runAppInBackground(Duration.ofSeconds(3));
     * 3. Sau khi App tự động khôi phục lại màn hình chính (Foreground):
     * Assert rằng ứng dụng không bị crash và màn hình Products vẫn hiển thị nguyên
     * vẹn.
     */
    @Test(priority = 5, description = "Bài tập 5: Đưa App xuống chạy nền 3 giây rồi kích hoạt lại kiểm tra tính ổn định")
    @Story("App Lifecycle & Backgrounding")
    public void practice05_AppBackgroundAndResume() {
        LogUtils.info(">>> BẮT ĐẦU BÀI TẬP 5: CHẠY NỀN APP VÀ KHÔI PHỤC (BACKGROUND & RESUME) <<<");

        AppiumDriver driver = DriverManager.getDriver();

        // TODO BƯỚC 1: Gọi hàm runAppInBackground trong 3 giây qua interface
        // InteractsWithApps

        // TODO BƯỚC 2: Assert màn hình Products vẫn hiển thị bình thường sau khi quay
        // lại Foreground

    }

    // ==========================================================================
    // BÀI TẬP 6: ĐỌC THUỘC TÍNH ELEMENT KHÁC BIỆT GIỮA ANDROID VÀ IOS
    // ==========================================================================
    /**
     * ĐỀ BÀI 6:
     * 1. Định vị nút mở Menu ("open menu") dùng chung qua Accessibility ID.
     * 2. Lấy thuộc tính nhận diện của nút theo hệ điều hành:
     * - Trên Android: Lấy thuộc tính "content-desc" (hoặc "className").
     * - Trên iOS: Lấy thuộc tính "label" (hoặc "name").
     * 3. In kết quả ra LogUtils.info và Assert giá trị thu được khớp với "open
     * menu".
     */
    @Test(priority = 6, description = "Bài tập 6: Lấy và so sánh các thuộc tính text, content-desc (Android) vs label, name (iOS)")
    @Story("Element Attributes Inspection")
    public void practice06_InspectAttributesDifference() {
        LogUtils.info(">>> BẮT ĐẦU BÀI TẬP 6: PHÂN TÍCH THUỘC TÍNH ANDROID VS IOS <<<");

        AppiumDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // TODO BƯỚC 1: Định vị WebElement btnMenu qua AppiumBy.accessibilityId("open
        // menu")

        // TODO BƯỚC 2: Nếu là Android -> Lấy getAttribute("content-desc") và Assert

        // TODO BƯỚC 3: Nếu là iOS -> Lấy getAttribute("label") hoặc
        // getAttribute("name") và Assert

    }

    // ==========================================================================
    // ==========================================================================
    // ĐÁP ÁN THAM KHẢO (CHỈ MỞ KHI BỊ KẸT HOẶC CẦN ĐỐI CHIẾU)
    // ==========================================================================
    // ==========================================================================
    public static class SolutionReference {

        /**
         * Đáp án mẫu cho Bài tập 1
         */
        public static void solution01_InputAndHideKeyboard(AppiumDriver driver, WebDriverWait wait) {
            By txtUsername = (driver instanceof AndroidDriver)
                    ? AppiumBy.accessibilityId("Username input field")
                    : AppiumBy.iOSClassChain("**/XCUIElementTypeTextField[1]");

            WebElement userInput = wait.until(ExpectedConditions.visibilityOfElementLocated(txtUsername));
            userInput.clear();
            userInput.sendKeys("standard_user");

            if (driver instanceof AndroidDriver) {
                ((HidesKeyboard) driver).hideKeyboard();
            } else {
                try {
                    driver.findElement(AppiumBy.accessibilityId("Done")).click();
                } catch (Exception e) {
                    userInput.sendKeys("\n");
                }
            }

            String actualValue = (driver instanceof AndroidDriver)
                    ? userInput.getAttribute("text")
                    : userInput.getAttribute("value");
            Assert.assertEquals(actualValue, "standard_user");
        }

        /**
         * Đáp án mẫu cho Bài tập 2
         */
        public static void solution02_W3CSwipeGesture(AppiumDriver driver) {
            Dimension size = driver.manage().window().getSize();
            int screenWidth = size.getWidth();
            int screenHeight = size.getHeight();

            Point from = new Point(screenWidth / 2, (int) (screenHeight * 0.75));
            Point to = new Point(screenWidth / 2, (int) (screenHeight * 0.25));

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
            Sequence swipe = new Sequence(finger, 1);

            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), from.x, from.y));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(
                    finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), to.x, to.y));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            driver.perform(Collections.singletonList(swipe));
        }

        /**
         * Đáp án mẫu cho Bài tập 3
         */
        public static WebElement solution03_ScrollToElementDifference(AppiumDriver driver, String targetText) {
            WebElement targetElement;
            if (driver instanceof AndroidDriver) {
                String uiQuery = "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\""
                        + targetText + "\"))";
                targetElement = driver.findElement(AppiumBy.androidUIAutomator(uiQuery));
            } else {
                Map<String, Object> scrollParams = new HashMap<>();
                scrollParams.put("direction", "down");
                driver.executeScript("mobile: scroll", scrollParams);
                targetElement = driver.findElement(AppiumBy.iOSNsPredicateString(
                        "label CONTAINS '" + targetText + "' OR name CONTAINS '" + targetText + "'"));
            }
            Assert.assertTrue(targetElement.isDisplayed());
            return targetElement;
        }

        /**
         * Đáp án mẫu cho Bài tập 4
         */
        public static void solution04_HardwareBackVsNavigationBack(AppiumDriver driver) {
            if (driver instanceof AndroidDriver) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                try {
                    driver.findElement(AppiumBy.accessibilityId("open menu")).click();
                    driver.findElement(AppiumBy.accessibilityId("menu item catalog")).click();
                } catch (Exception e) {
                    driver.findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`label == 'Back'`]")).click();
                }
            }
        }

        /**
         * Đáp án mẫu cho Bài tập 5
         */
        public static void solution05_AppBackgroundAndResume(AppiumDriver driver) {
            ((InteractsWithApps) driver).runAppInBackground(Duration.ofSeconds(3));
            ProductsScreen productsScreen = new ProductsScreen();
            Assert.assertTrue(productsScreen.isProductPageDisplayed());
        }

        /**
         * Đáp án mẫu cho Bài tập 6
         */
        public static void solution06_InspectAttributesDifference(AppiumDriver driver, WebDriverWait wait) {
            WebElement btnMenu = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("open menu")));
            if (driver instanceof AndroidDriver) {
                String contentDesc = btnMenu.getAttribute("content-desc");
                Assert.assertEquals(contentDesc, "open menu");
            } else {
                String label = btnMenu.getAttribute("label");
                String name = btnMenu.getAttribute("name");
                Assert.assertTrue("open menu".equals(label) || "open menu".equals(name));
            }
        }
    }
}
