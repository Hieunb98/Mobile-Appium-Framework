package com.appium.utils;

import net.datafaker.Faker;

import java.util.Locale;

public final class DataFakerUtils {
    private static final Faker FAKER = new Faker(new Locale("vi"));

    private DataFakerUtils() {}

    public static Faker getFaker() {
        return FAKER;
    }

    public static String getFullName() {
        return FAKER.name().fullName();
    }

    public static String getRandomEmail() {
        long timestamp = System.currentTimeMillis();
        return "auto_test_" + timestamp + "@automation.test";
    }

    public static String getPhoneNumber() {
        return FAKER.phoneNumber().phoneNumber();
    }

    public static String getStreetAddress() {
        return FAKER.address().streetAddress();
    }

    public static String getCity() {
        return FAKER.address().city();
    }

    public static String getZipCode() {
        return FAKER.address().zipCode();
    }
}
