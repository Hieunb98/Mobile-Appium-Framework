package com.appium.enums;

/**
 * FailureHandling quy định hành vi xử lý khi một bước kiểm tra (assertion/verification) thất bại.
 * - STOP_ON_FAILURE: Dừng test case ngay lập tức (Hard Assert).
 * - CONTINUE_ON_FAILURE: Ghi nhận lỗi vào SoftAssert và tiếp tục chạy các bước sau.
 * - OPTIONAL: Bỏ qua lỗi và tiếp tục (thông báo cảnh báo).
 */
public enum FailureHandling {
    STOP_ON_FAILURE,
    CONTINUE_ON_FAILURE,
    OPTIONAL
}
