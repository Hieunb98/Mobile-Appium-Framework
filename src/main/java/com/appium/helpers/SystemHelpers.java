package com.appium.helpers;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * SystemHelpers chứa các hàm tiện ích thao tác hệ thống, thư mục, đường dẫn (chuẩn CRM-frameWork).
 */
public final class SystemHelpers {

    private SystemHelpers() {
    }

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    /**
     * Lấy đường dẫn thư mục gốc của project kết thúc bằng dấu phân cách thư mục.
     * @return Đường dẫn thư mục hiện tại kèm File.separator
     */
    public static String getCurrentDir() {
        return System.getProperty("user.dir") + File.separator;
    }

    /**
     * Tạo thư mục nếu chưa tồn tại.
     * @param path Đường dẫn thư mục cần tạo
     */
    public static void createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                System.out.println("Folder created: " + folder.getAbsolutePath());
            } else {
                System.out.println("Failed to create folder: " + folder.getAbsolutePath());
            }
        }
    }

    /**
     * Tạo slug từ chuỗi ký tự (hỗ trợ đặt tên file screenshot, video).
     */
    public static String makeSlug(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        String noWhiteSpace = WHITESPACE.matcher(input).replaceAll("_");
        String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Tách chuỗi thành danh sách.
     */
    public static ArrayList<String> splitString(String str, String valueSplit) {
        ArrayList<String> arrayListString = new ArrayList<>();
        for (String s : str.split(valueSplit, 0)) {
            arrayListString.add(s);
        }
        return arrayListString;
    }

    /**
     * Xóa dấu tiếng Việt khỏi chuỗi.
     */
    public static String removeAccent(String text) {
        if (text == null) return null;
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");
        withoutDiacritics = withoutDiacritics.replace("đ", "d").replace("Đ", "D");
        return withoutDiacritics;
    }
}
