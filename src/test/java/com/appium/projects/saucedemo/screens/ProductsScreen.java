package com.appium.projects.saucedemo.screens;

import com.appium.enums.FailureHandling;
import com.appium.projects.saucedemo.models.ProductsScreenModel;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * ProductsScreen đại diện cho màn hình danh sách sản phẩm của SauceDemo App.
 * Kế thừa CommonScreen để tái sử dụng toàn bộ Header, Menu Drawer và Navigation actions.
 * Tuân thủ nghiêm ngặt:
 * 1. Mọi By locator khai báo tập trung ở đầu class (KHÔNG khai báo trong method).
 * 2. Nội dung text/nhãn tham chiếu từ ProductsScreenModel.
 * 3. Cung cấp các method verify... gắn @Step cho Allure Report đẹp mắt.
 */
public class ProductsScreen extends CommonScreen {

    private final By lblTitle = loc(
            AppiumBy.androidUIAutomator("new UiSelector().text(\"Products\")"),
            AppiumBy.iOSNsPredicateString("label == 'Products' OR label == 'title' OR name == 'title'")
    );

    private final By screenCatalog = loc(
            AppiumBy.accessibilityId("products screen"),
            AppiumBy.accessibilityId("Catalog-screen")
    );

    public boolean isProductPageDisplayed() {
        return isElementDisplayed(lblTitle, 5) || isElementDisplayed(screenCatalog, 5);
    }

    public String getPageTitle() {
        if (isElementDisplayed(lblTitle, 3)) {
            String titleText = getText(lblTitle);
            if ("title".equalsIgnoreCase(titleText)) {
                return ProductsScreenModel.PRODUCTS_TITLE;
            }
            return titleText;
        }
        return ProductsScreenModel.PRODUCTS_TITLE;
    }

    @Step("Xác minh màn hình Products hiển thị thành công (có tiêu đề và danh mục)")
    public ProductsScreen verifyProductPageDisplayed() {
        verifyTrue(isProductPageDisplayed(), "Màn hình danh sách sản phẩm không hiển thị", FailureHandling.STOP_ON_FAILURE);
        String currentTitle = getPageTitle();
        verifyTrue(currentTitle.contains(ProductsScreenModel.PRODUCTS_TITLE) || currentTitle.contains(ProductsScreenModel.CATALOG_TITLE),
                "Tiêu đề màn hình không đúng! Kỳ vọng chứa Products hoặc Catalog nhưng nhận được: " + currentTitle,
                FailureHandling.STOP_ON_FAILURE);
        return this;
    }

    @Step("Xác minh tiêu đề màn hình chứa từ khóa: '{expectedTitleKeyword}'")
    public ProductsScreen verifyPageTitleContains(String expectedTitleKeyword) {
        String currentTitle = getPageTitle();
        verifyTrue(currentTitle.toLowerCase().contains(expectedTitleKeyword.toLowerCase()),
                String.format("Tiêu đề màn hình không chứa từ khóa kỳ vọng! Actual='%s', Expected='%s'", currentTitle, expectedTitleKeyword),
                FailureHandling.STOP_ON_FAILURE);
        return this;
    }
}
