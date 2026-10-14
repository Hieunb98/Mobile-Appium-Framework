package com.appium.projects.saucedemo.screens;

import com.appium.base.BaseScreen;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * CommonScreen — Screen đại diện cho các thành phần UI chung trên Mobile App:
 * (Top Navigation Bar, Side Menu Drawer, Bottom Tab Bar, Giỏ hàng).
 * <p>
 * Áp dụng 2 kỹ thuật thiết kế chuẩn:
 * 1. Lazy Initialization (Null-Safe Screen Getter): Khởi tạo lười các Screen con khi cần,
 *    tránh tạo nhiều instance rác và ngăn ngừa NullPointerException.
 * 2. Page Chaining (Fluent Interface): Các phương thức điều hướng trả về chính Screen Object đích
 *    (thông qua hàm getScreen tương ứng).
 */
public class CommonScreen extends BaseScreen {

    // =========================================================================
    // INSTANCE HOLDER CHO CÁC SCREEN CON (LAZY INITIALIZATION)
    // =========================================================================
    public LoginScreen loginScreen;
    public ProductsScreen productsScreen;
    public CartScreen cartScreen;
    public ProfileScreen profileScreen;

    // =========================================================================
    // 1. TOP NAVIGATION BAR & HEADER LOCATORS
    // =========================================================================
    public final By btnOpenMenu = loc(
            AppiumBy.accessibilityId("open menu"),
            AppiumBy.accessibilityId("More-tab-item")
    );

    public final By btnCartBadge = loc(
            AppiumBy.accessibilityId("cart badge"),
            AppiumBy.accessibilityId("Cart-tab-item")
    );

    // =========================================================================
    // 2. SIDE MENU DRAWER ITEMS (Android) & MORE MENU ITEMS (iOS)
    // =========================================================================
    public final By menuCatalog = loc(
            AppiumBy.accessibilityId("menu item catalog"),
            AppiumBy.accessibilityId("Catalog-tab-item")
    );

    public final By menuLogIn = loc(
            AppiumBy.accessibilityId("menu item log in"),
            AppiumBy.accessibilityId("Login Button")
    );

    public final By menuLogOut = loc(
            AppiumBy.accessibilityId("menu item log out"),
            AppiumBy.accessibilityId("LogOut-menu-item")
    );

    public final By menuAbout = loc(
            AppiumBy.accessibilityId("menu item about"),
            AppiumBy.iOSClassChain("**/XCUIElementTypeOther[`name == 'menu item about'`]")
    );

    public final By menuResetAppState = loc(
            AppiumBy.accessibilityId("menu item reset app"),
            AppiumBy.iOSClassChain("**/XCUIElementTypeOther[`name == 'menu item reset app'`]")
    );

    public final By menuWebview = loc(
            AppiumBy.accessibilityId("menu item webview"),
            AppiumBy.iOSClassChain("**/XCUIElementTypeOther[`name == 'menu item webview'`]")
    );

    public final By menuBiometrics = loc(
            AppiumBy.accessibilityId("menu item biometrics"),
            AppiumBy.iOSClassChain("**/XCUIElementTypeOther[`name == 'menu item biometrics'`]")
    );

    // =========================================================================
    // 3. BOTTOM TAB BAR LOCATORS (Hỗ trợ cấu hình Native / Bottom Nav nếu có)
    // =========================================================================
    public final By tabBottomHome = loc(
            AppiumBy.xpath("//*[@content-desc='Home' or @text='Home' or @content-desc='tab-home']"),
            AppiumBy.iOSNsPredicateString("label == 'Home' OR name == 'Home'")
    );

    public final By tabBottomCart = loc(
            AppiumBy.xpath("//*[@content-desc='Cart' or @text='Cart' or @content-desc='tab-cart']"),
            AppiumBy.iOSNsPredicateString("label == 'Cart' OR name == 'Cart'")
    );

    public final By tabBottomProfile = loc(
            AppiumBy.xpath("//*[@content-desc='Profile' or @text='Profile' or @content-desc='tab-profile']"),
            AppiumBy.iOSNsPredicateString("label == 'Profile' OR name == 'Profile'")
    );

    // =========================================================================
    // LAZY INITIALIZATION SCREEN GETTERS (Null-Safe)
    // =========================================================================

    public LoginScreen getLoginScreen() {
        if (loginScreen == null) {
            loginScreen = new LoginScreen();
        }
        return loginScreen;
    }

    public ProductsScreen getProductsScreen() {
        if (productsScreen == null) {
            productsScreen = new ProductsScreen();
        }
        return productsScreen;
    }

    public CartScreen getCartScreen() {
        if (cartScreen == null) {
            cartScreen = new CartScreen();
        }
        return cartScreen;
    }

    public ProfileScreen getProfileScreen() {
        if (profileScreen == null) {
            profileScreen = new ProfileScreen();
        }
        return profileScreen;
    }

    // =========================================================================
    // PAGE CHAINING NAVIGATION ACTIONS (FLUENT INTERFACE)
    // =========================================================================

    @Step("Mở Menu Drawer và điều hướng đến màn hình Đăng nhập (Login)")
    public LoginScreen openMenuAndGoToLogin() {
        click(btnOpenMenu);
        click(menuLogIn);
        return getLoginScreen();
    }

    @Step("Mở Menu Drawer và điều hướng đến màn hình Danh mục sản phẩm (Catalog)")
    public ProductsScreen openMenuAndGoToCatalog() {
        click(btnOpenMenu);
        click(menuCatalog);
        return getProductsScreen();
    }

    @Step("Mở màn hình Danh sách sản phẩm (ProductsScreen)")
    public ProductsScreen openProductsScreen() {
        if (isElementDisplayed(tabBottomHome)) {
            click(tabBottomHome);
        } else {
            click(btnOpenMenu);
            click(menuCatalog);
        }
        return getProductsScreen();
    }

    @Step("Bấm vào icon Giỏ hàng trên Header để mở màn hình Giỏ hàng (Cart)")
    public CartScreen openCartScreen() {
        click(btnCartBadge);
        return getCartScreen();
    }

    @Step("Điều hướng đến màn hình Thông tin tài khoản (Profile)")
    public ProfileScreen openProfileScreen() {
        if (isElementDisplayed(tabBottomProfile)) {
            click(tabBottomProfile);
        } else {
            // Mở qua menu nếu app bố trí Profile trong menu drawer
            click(btnOpenMenu);
            if (isElementDisplayed(menuLogIn)) {
                click(menuLogIn);
            }
        }
        return getProfileScreen();
    }

    @Step("Mở Menu Drawer và thực hiện Đăng xuất (Logout)")
    public LoginScreen openMenuAndLogout() {
        click(btnOpenMenu);
        click(menuLogOut);
        try {
            By btnConfirmLogout = loc(
                    AppiumBy.xpath("//*[@text='LOG OUT' or @text='Log Out' or @resource-id='android:id/button1']"),
                    AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`name == 'Log Out'`]")
            );
            if (checkElementExist(btnConfirmLogout)) {
                click(btnConfirmLogout);
            }
            By btnOkAlert = loc(
                    AppiumBy.xpath("//*[@text='OK' or @resource-id='android:id/button1']"),
                    AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`name == 'OK'`]")
            );
            if (checkElementExist(btnOkAlert)) {
                click(btnOkAlert);
            }
        } catch (Exception ignored) {}
        return getLoginScreen();
    }
}
