package com.appium.listeners;

import com.appium.constants.FrameworkConstants;
import com.appium.utils.LogUtils;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {
    private int count = 0;
    private static final int MAX_TRY = FrameworkConstants.RETRY_COUNT;

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (!iTestResult.isSuccess()) {
            if (count < MAX_TRY) {
                count++;
                LogUtils.warn("Đang retry lại test case: " + iTestResult.getName() + " (lần " + count + "/" + MAX_TRY + ")");
                return true;
            }
        }
        return false;
    }
}
