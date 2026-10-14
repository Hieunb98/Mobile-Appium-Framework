package com.appium.projects.saucedemo.models;

/**
 * ProductsScreenModel lưu trữ các giá trị tĩnh, text và nhãn của màn hình Products/Catalog.
 * Tách biệt hoàn toàn dữ liệu văn bản ra khỏi Screen Object theo ScreenModel Pattern.
 */
public final class ProductsScreenModel {

    private ProductsScreenModel() {
        // Private constructor ngăn việc khởi tạo đối tượng tiện ích
    }

    // Tiêu đề màn hình
    public static final String PRODUCTS_TITLE = "Products";
    public static final String CATALOG_TITLE = "Catalog";

    // Nhãn trên menu / tabs
    public static final String TAB_CATALOG = "Catalog";
    public static final String TAB_CART = "Cart";
    public static final String TAB_MORE = "More";
}
