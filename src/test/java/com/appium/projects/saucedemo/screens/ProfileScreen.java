package com.appium.projects.saucedemo.screens;

import com.appium.base.BaseScreen;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * ProfileScreen đại diện cho màn hình Thông tin tài khoản / Profile của Mobile App.
 * Nằm trong cấu trúc module Project: com.appium.projects.saucedemo.screens
 */
public class ProfileScreen extends BaseScreen {

    private final By lblTitle = loc(
            AppiumBy.xpath("//android.widget.TextView[@text='Profile' or @text='Account']"),
            AppiumBy.iOSNsPredicateString("label == 'Profile' OR name == 'Profile'")
    );

    private final By btnLogout = loc(
            AppiumBy.accessibilityId("Log Out button"),
            AppiumBy.xpath("//android.widget.TextView[@text='Log Out']")
    );

    public boolean isProfilePageDisplayed() {
        return isElementDisplayed(lblTitle);
    }

    public ProfileScreen verifyProfilePageDisplayed() {
        verifyTrue(isProfilePageDisplayed(), "Màn hình Profile không hiển thị");
        return this;
    }

    @Step("Đăng xuất khỏi Profile")
    public LoginScreen clickLogout() {
        click(btnLogout);
        return new LoginScreen();
    }
}
