package com.appium.projects.saucedemo.models;

/**
 * LoginScreenModel lưu trữ các giá trị tĩnh, text, nhãn và thông báo lỗi của màn hình Login.
 * Tách biệt hoàn toàn dữ liệu văn bản (Content/Texts) ra khỏi Screen Object theo ScreenModel Pattern.
 */
public final class LoginScreenModel {

    private LoginScreenModel() {
        // Private constructor ngăn việc khởi tạo đối tượng tiện ích
    }

    // Tài khoản mẫu hệ thống
    public static final String STANDARD_USER = "bob@example.com";
    public static final String LOCKED_USER = "alice@example.com";
    public static final String DEFAULT_PASSWORD = "10203040";

    // Tiêu đề và nhãn form
    public static final String HEADER_TITLE = "Login";
    public static final String USERNAME_PLACEHOLDER = "Username";
    public static final String PASSWORD_PLACEHOLDER = "Password";
    public static final String LOGIN_BUTTON_TEXT = "Login";

    // Các thông báo lỗi xác thực (Error Messages)
    public static final String ERROR_LOCKED_OUT = "Sorry, this user has been locked out.";
    public static final String ERROR_INVALID_CREDENTIALS = "Provided credentials do not match any user in this service.";
    public static final String ERROR_USERNAME_REQUIRED = "Username is required";
    public static final String ERROR_PASSWORD_REQUIRED = "Password is required";
}
