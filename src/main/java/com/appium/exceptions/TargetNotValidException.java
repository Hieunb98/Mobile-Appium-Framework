package com.appium.exceptions;

public class TargetNotValidException extends IllegalStateException {
    public TargetNotValidException(String target) {
        super(String.format("Target %s không hợp lệ. Vui lòng chọn LOCAL hoặc REMOTE trong config.properties.", target));
    }
}
