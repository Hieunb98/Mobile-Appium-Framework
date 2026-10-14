package com.appium.projects.saucedemo.testcases;

import com.appium.common.BaseTest;
import com.appium.projects.saucedemo.models.LoginScreenModel;
import com.appium.projects.saucedemo.screens.ProductsScreen;
import com.appium.utils.DataFakerUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

/**
 * LoginTest chứa các test cases kiểm thử tính năng đăng nhập trên SauceDemo App.
 * Áp dụng triệt để:
 * 1. Fluent Page Chaining (không new Screen rải rác).
 * 2. Sử dụng ScreenModel cho Test Data và expected messages.
 * 3. Chuyển toàn bộ Assertions thành các verification methods trong Screen Object có gắn @Step cho Allure.
 */
@Epic("SauceLabs My Demo App")
@Feature("Authentication Feature")
public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "TC01: Đăng nhập thành công với tài khoản chuẩn (Happy Path)")
    @Story("Đăng nhập hợp lệ")
    public void testLogin_ValidCredentials() {
        productsScreen
                .openMenuAndGoToLogin()
                .loginSuccess(LoginScreenModel.STANDARD_USER, LoginScreenModel.DEFAULT_PASSWORD)
                .verifyProductPageDisplayed()
                .openMenuAndLogout();
    }

    @Test(priority = 2, description = "TC02: Đăng nhập thất bại với tài khoản bị khóa (Locked out user)")
    @Story("Đăng nhập tài khoản bị khóa")
    public void testLogin_LockedUser() {
        productsScreen
                .openMenuAndGoToLogin()
                .loginFail(LoginScreenModel.LOCKED_USER, LoginScreenModel.DEFAULT_PASSWORD)
                .verifyErrorMessageContains("locked");
    }

    @Test(priority = 3, description = "TC03: Đăng nhập thất bại với tài khoản ngẫu nhiên không tồn tại")
    @Story("Đăng nhập tài khoản không tồn tại")
    public void testLogin_InvalidCredentials() {
        String randomEmail = DataFakerUtils.getRandomEmail();
        productsScreen
                .openMenuAndGoToLogin()
                .loginFail(randomEmail, "wrongpassword123")
                .verifyErrorMessageDisplayed();
    }

    @Test(priority = 4, description = "TC04: Bỏ trống cả 2 trường Username và Password khi bấm Login")
    @Story("Validation để trống form")
    public void testLogin_EmptyCredentials() {
        productsScreen
                .openMenuAndGoToLogin()
                .loginFail("", "")
                .verifyErrorMessageDisplayed();
    }
}
