package com.appium.enums;

/**
 * Môi trường thực thi Appium Test:
 * - LOCAL: Máy ảo hoặc thiết bị thật cắm trực tiếp vào máy tính chạy test.
 * - REMOTE: Chạy qua Server Appium từ xa, Grid hoặc Cloud Device Farm (BrowserStack, SauceLabs).
 */
public enum Target {
    LOCAL,
    REMOTE
}
