package com.appium.projects.saucedemo.screens;

import com.appium.base.BaseScreen;
import com.appium.enums.FailureHandling;
import com.appium.projects.saucedemo.models.LoginScreenModel;
import com.appium.utils.LogUtils;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * LoginScreen đại diện cho màn hình đăng nhập của SauceDemo App.
 * Kế thừa CommonScreen (BaseScreen > CommonScreen > Screen cụ thể).
 * Tuân thủ nghiêm ngặt:
 * 1. Mọi By locator khai báo tập trung ở đầu class (KHÔNG khai báo trong
 * method).
 * 2. Nội dung text/nhãn tham chiếu từ LoginScreenModel.
 * 3. Cung cấp các method verify... gắn @Step cho Allure Report đẹp mắt.
 */
public class LoginScreen extends CommonScreen {

    private final By txtUsername = loc(
            AppiumBy.accessibilityId("Username input field"),
            AppiumBy.iOSClassChain("**/XCUIElementTypeTextField"));
    private final By txtPassword = loc(
            AppiumBy.accessibilityId("Password input field"),
            AppiumBy.iOSClassChain("**/XCUIElementTypeSecureTextField"));
    private final By btnLogin = loc(
            AppiumBy.accessibilityId("Login button"),
            AppiumBy.accessibilityId("Login"));
    private final By lblErrorMessage = loc(
            AppiumBy.xpath("//*[@content-desc='generic-error-message']//android.widget.TextView | //*[@content-desc='generic-error-message']"),
            AppiumBy.iOSNsPredicateString(
                    "label CONTAINS 'locked' OR label CONTAINS 'Sorry' OR label CONTAINS 'match' OR label CONTAINS 'required' OR label CONTAINS 'Provided'"));
    private final By btnDismissSavePrompt = loc(
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Never\").textContains(\"Cancel\")"),
            AppiumBy.iOSNsPredicateString(
                    "label == 'Để sau' OR label == 'Not Now' OR label == 'Never' OR label == 'Lưu'"));

    public boolean isLoginScreenDisplayed() {
        return isElementDisplayed(txtUsername) && isElementDisplayed(btnLogin);
    }

    @Step("Nhập tên đăng nhập: {username}")
    public LoginScreen enterUsername(String username) {
        setText(txtUsername, username);
        hideKeyboard();
        return this;
    }

    @Step("Nhập mật khẩu: {password}")
    public LoginScreen enterPassword(String password) {
        setText(txtPassword, password);
        hideKeyboard();
        return this;
    }

    @Step("Đóng popup lưu mật khẩu hệ thống nếu có")
    public void handleSavePasswordPrompt() {
        try {
            getDriver().switchTo().alert().dismiss();
            LogUtils.info("Đã đóng System Alert Lưu mật khẩu bằng alert.dismiss()");
        } catch (Exception e1) {
            try {
                if (checkElementExist(btnDismissSavePrompt)) {
                    click(btnDismissSavePrompt);
                    LogUtils.info("Đã đóng popup Lưu mật khẩu của hệ thống");
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Step("Bấm nút Login")
    public void clickLogin() {
        click(btnLogin);
        handleSavePasswordPrompt();
    }

    @Step("Đăng nhập thành công với tài khoản: {username}")
    public ProductsScreen loginSuccess(String username, String password) {
        if (!username.isEmpty()) {
            setText(txtUsername, username);
        }
        if (!password.isEmpty()) {
            setText(txtPassword, password);
        }
        hideKeyboard();
        click(btnLogin);
        handleSavePasswordPrompt();
        return getProductsScreen();
    }

    @Step("Đăng nhập thất bại với tài khoản: {username}")
    public LoginScreen loginFail(String username, String password) {
        if (!username.isEmpty()) {
            setText(txtUsername, username);
        }
        if (!password.isEmpty()) {
            setText(txtPassword, password);
        }
        hideKeyboard();
        click(btnLogin);
        return this;
    }

    public String getErrorMessage() {
        String msg = getText(lblErrorMessage);
        if (msg == null || msg.trim().isEmpty()) {
            msg = getAttribute(lblErrorMessage, "text");
        }
        if (msg == null || msg.trim().isEmpty()) {
            msg = getAttribute(lblErrorMessage, "content-desc");
        }
        return msg != null ? msg : "";
    }

    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(lblErrorMessage);
    }

    @Step("Xác minh màn hình Đăng nhập hiển thị đầy đủ trường nhập")
    public LoginScreen verifyLoginScreenDisplayed() {
        verifyTrue(isLoginScreenDisplayed(), "Màn hình Login không hiển thị đúng các trường nhập",
                FailureHandling.STOP_ON_FAILURE);
        return this;
    }

    @Step("Xác minh thông báo lỗi hiển thị trên màn hình")
    public LoginScreen verifyErrorMessageDisplayed() {
        verifyTrue(isErrorMessageDisplayed(), "Không hiển thị thông báo lỗi trên màn hình",
                FailureHandling.STOP_ON_FAILURE);
        return this;
    }

    @Step("Xác minh nội dung thông báo lỗi chứa từ khóa: '{expectedKeyword}'")
    public LoginScreen verifyErrorMessageContains(String expectedKeyword) {
        verifyTrue(isErrorMessageDisplayed(), "Không hiển thị thông báo lỗi trên màn hình",
                FailureHandling.STOP_ON_FAILURE);
        String actualMsg = getErrorMessage().toLowerCase();
        verifyTrue(actualMsg.contains(expectedKeyword.toLowerCase()),
                String.format("Nội dung thông báo lỗi không chứa từ khóa kỳ vọng! Actual='%s', Expected='%s'",
                        actualMsg, expectedKeyword),
                FailureHandling.STOP_ON_FAILURE);
        return this;
    }
}
