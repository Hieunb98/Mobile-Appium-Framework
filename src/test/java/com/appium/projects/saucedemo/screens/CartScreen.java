package com.appium.projects.saucedemo.screens;

import com.appium.base.BaseScreen;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * CartScreen đại diện cho màn hình Giỏ hàng của SauceDemo App.
 * Nằm trong cấu trúc module Project: com.appium.projects.saucedemo.screens
 */
public class CartScreen extends BaseScreen {

    private final By lblTitle = loc(
            AppiumBy.xpath("//android.widget.TextView[@text='My Cart']"),
            AppiumBy.iOSNsPredicateString("label == 'My Cart' OR name == 'My Cart'")
    );

    private final By btnProceedCheckout = loc(
            AppiumBy.accessibilityId("Proceed To Checkout button"),
            AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`label == 'Proceed To Checkout'`]")
    );

    private final By btnContinueShopping = loc(
            AppiumBy.accessibilityId("Continue Shopping button"),
            AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`label == 'Continue Shopping'`]")
    );

    private final By lblEmptyCartMessage = loc(
            AppiumBy.xpath("//android.widget.TextView[contains(@text,'No Items')]"),
            AppiumBy.iOSNsPredicateString("label CONTAINS 'No Items'")
    );

    public boolean isCartPageDisplayed() {
        return isElementDisplayed(lblTitle);
    }

    public String getPageTitle() {
        return getText(lblTitle);
    }

    public CartScreen verifyCartPageDisplayed() {
        verifyTrue(isCartPageDisplayed(), "Màn hình Giỏ hàng (My Cart) không hiển thị");
        return this;
    }

    public boolean isCartEmpty() {
        return isElementDisplayed(lblEmptyCartMessage);
    }

    @Step("Bấm nút 'Proceed To Checkout'")
    public LoginScreen clickProceedToCheckout() {
        click(btnProceedCheckout);
        return new LoginScreen();
    }

    @Step("Bấm nút 'Continue Shopping' để quay lại trang sản phẩm")
    public ProductsScreen clickContinueShopping() {
        click(btnContinueShopping);
        return new ProductsScreen();
    }
}
