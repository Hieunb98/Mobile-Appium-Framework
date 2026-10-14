# 📱 Mobile Test Automation Framework (Appium + Java + TestNG)

Dự án kiểm thử tự động ứng dụng di động (Mobile Automation) chuyên nghiệp, xây dựng theo mô hình **Page Object Model (POM)** trên nền tảng **Appium 2x/3x**, **Java 17**, **TestNG**, và **Allure Report**.

---

## 🛠️ Tech Stack & Thư viện sử dụng

* **Ngôn ngữ:** Java 17
* **Mobile Automation:** Appium Java Client (`9.2.2`)
* **Test Runner:** TestNG (`7.10.2`)
* **Báo cáo kiểm thử:** Allure Report (`2.27.0`)
* **Quản lý Log:** Log4j2 (`2.23.1`)
* **Data-Driven Testing:** Apache POI (`5.2.5`) đọc file Excel + Jackson JSON
* **Sinh dữ liệu kiểm thử:** DataFaker (`2.2.2`)
* **Build Tool:** Maven

---

## 📱 Phân loại ứng dụng Mobile (Native vs React Native vs Flutter)

Dự án mẫu hiện tại kiểm thử trên **Sauce Labs My Demo App (React Native)** (`Android-MyDemoAppRN.1.3.0.build-244.apk`). Dưới đây là bảng đối chiếu giữa 3 công nghệ ứng dụng di động phổ biến nhất hiện nay:

| Tiêu chí | 1. Native App | 2. React Native App | 3. Flutter App |
| :--- | :--- | :--- | :--- |
| **Ngôn ngữ code** | **Android:** Java / Kotlin<br>**iOS:** Swift / Objective-C | **JavaScript** / **TypeScript** | **Dart** (Google) |
| **Cơ chế hiển thị UI** | Sử dụng 100% Widget gốc HĐH Android / iOS | Code JS qua Bridge/Fabric ánh xạ ra Native View gốc | Tự vẽ pixel lên Canvas bằng engine Skia / Impeller |
| **Độ khó khi test (QA)** | ⭐/5 **(Dễ nhất)**<br>Element gốc, đầy đủ `resource-id`, phân cấp DOM rõ ràng | ⭐⭐/5 **(Khá dễ)**<br>Render ra Native view, bắt qua `content-desc` (từ `testID`) rất chuẩn | ⭐⭐⭐⭐⭐/5 **(Khó nhất)**<br>Thường chỉ thấy 1 View lớn, cần driver riêng hoặc bật Semantics |
| **Tại sao dự án dùng loại này?** | • **Hiệu năng cao nhất:** Tận dụng 100% phần cứng, camera, bảo mật ngân hàng, Game 3D | • **Tiết kiệm 40-50% chi phí & thời gian:** 1 codebase cho cả iOS & Android, cộng đồng JS khổng lồ | • **UI đồng nhất 100%:** Giống hệt nhau trên mọi máy, mượt mà 60-120 FPS, Hot Reload cực nhanh |
| **Bản Demo thực tế** | `my-demo-app-android` | `my-demo-app-rn` *(File trong thư mục `apps/`)* | `flutter_gallery` |
| **Appium Driver cần dùng** | `UiAutomator2` / `XCUITest` | `UiAutomator2` / `XCUITest` | `appium-flutter-integration-driver` |
| **Thuộc tính Locator tối ưu** | `resource-id` | `accessibility-id` (từ `testID`) | `ValueKey` hoặc `SemanticsLabel` |

---

## 📂 Cấu trúc dự án (Project Architecture)

```text
Mobile-Appium-Framework/
 ├── apps/
 │    └── mydemoapp.apk              # Ứng dụng mẫu Sauce Labs My Demo App
 ├── src/main/java/com/appium/
 │    ├── base/                      # BaseScreen (Core Base UI Engine: Wait, Click, SendKeys, Gestures, SoftAssert)
 │    ├── config/                    # Configuration, ConfigFactory (Owner Type-Safe)
 │    ├── constants/                 # FrameworkConstants (Đường dẫn, timeout, báo cáo)
 │    ├── driver/                    # DriverManager (ThreadLocal), TargetFactory, DeviceFactory (Enum Android/iOS)
 │    ├── enums/                     # Platform, Target, FailureHandling
 │    ├── exceptions/                # Custom Exceptions
 │    ├── helpers/                   # CaptureHelpers, ExcelHelpers, SystemHelpers
 │    ├── reports/                   # AllureManager, ExtentReportManager, ExtentTestManager
 │    └── utils/                     # LogUtils (Log4j2), DataFakerUtils (Random data)
 ├── src/test/java/com/appium/
 │    ├── common/                    # BaseTest (@BeforeSuite, @BeforeMethod, @AfterMethod)
 │    ├── listeners/                 # TestListener (Allure + ExtentReports + Video/Screenshot), Retry
 │    └── projects/                  # QUẢN LÝ DỰ ÁN (MULTI-PROJECT SUPPORT)
 │         └── saucedemo/            # Module Sauce Labs My Demo App
 │              ├── models/          # ScreenModel: Lưu trữ text tĩnh, labels, error messages, demo users
 │              ├── screens/         # Page Objects 3 tầng: CommonScreen, LoginScreen, ProductsScreen...
 │              └── testcases/       # Kịch bản kiểm thử: LoginTest...
 ├── src/test/resources/
 │    ├── config.properties          # File cấu hình thiết bị (platform, udid, appium url, timeout)
 │    ├── testng.xml                 # Test Suite điều phối thực thi
 │    └── testdata/                  # Dữ liệu Excel / JSON
 ├── pom.xml
 └── README.md
```

---

## 🏗️ Chuẩn Kiến Trúc 3 Tầng & ScreenModel Pattern (Quy Tắc Bắt Buộc)

Dự án áp dụng nghiêm ngặt kiến trúc 3 tầng kế thừa kết hợp **ScreenModel Pattern** nhằm đảm bảo khả năng mở rộng, ổn định và báo cáo Allure trực quan:

```text
      BaseScreen (Hạ tầng driver, click, setText, wait, cử chỉ native, SoftAssert)
          ▲
          │ extends
     CommonScreen (Header, Navigation Bar, Menu Drawer, Bottom Tabs + Lazy Init get...Screen())
          ▲
          │ extends
   ┌──────┴────────────────────────┬────────────────────────┐
LoginScreen                  ProductsScreen             CartScreen
```

### 1. Phân Tách Trách Nhiệm 4 Thành Phần:
* **`[Screen]Model.java`:** Lưu trữ 100% dữ liệu tĩnh (labels, texts hiển thị, error messages, placeholders, credentials mẫu). Screen class và Test class không hardcode chuỗi text.
* **`[Screen]Screen.java`:** Kế thừa `CommonScreen`. **CHỈ chứa Locators + Action methods + Verification methods**.
* **`[Screen]Test.java`:** Kế thừa `BaseTest`. Điều phối luồng kiểm thử dạng **Fluent Chaining**.
* **`Test Data & Utils`:** Dữ liệu sinh động (DataFaker, DataProvider, JSON/Excel).

### 2. Quy Tắc "CẤM" Trong Codebase:
* ❌ **CẤM `new Screen()` trong `@Test` methods:** Test class lấy sẵn Entry Screen từ `BaseTest` (ví dụ `productsScreen = commonScreen.getProductsScreen()`) và luân chuyển màn hình qua chuỗi hành động.
* ❌ **CẤM khai báo Locator inline trong method:** 100% `By` locator phải khai báo tập trung ở đầu Screen class (`private final By`).
* ❌ **CẤM Assertions thô rải rác trong Test class:** Toàn bộ kiểm tra kết quả kỳ vọng phải đóng gói thành các method `verify...` nằm trong Screen Object có gắn `@Step("Xác minh...")` để Allure Report hiển thị từng bước kiểm thử chi tiết.

### 3. Ví Dụ Triển Khai Chuẩn:

**Trong `LoginScreen.java`:**
```java
public class LoginScreen extends CommonScreen {
    // Locator tập trung ở đầu class
    private final By txtUsername = loc(AppiumBy.accessibilityId("Username input field"), AppiumBy.iOSClassChain("**/XCUIElementTypeTextField[1]"));
    private final By btnLogin = AppiumBy.accessibilityId("Login button");

    @Step("Đăng nhập thành công với tài khoản: {username}")
    public ProductsScreen loginSuccess(String username, String password) {
        setText(txtUsername, username);
        setText(txtPassword, password);
        hideKeyboard();
        click(btnLogin);
        return getProductsScreen(); // Page Chaining qua Lazy Getter kế thừa từ CommonScreen
    }

    @Step("Xác minh thông báo lỗi hiển thị")
    public LoginScreen verifyErrorMessageDisplayed() {
        Assert.assertTrue(isElementDisplayed(lblErrorMessage), "Lỗi không hiển thị");
        return this;
    }
}
```

**Trong `LoginTest.java`:**
```java
public class LoginTest extends BaseTest {
    @Test(description = "TC01: Đăng nhập thành công với tài khoản chuẩn")
    public void testLogin_ValidCredentials() {
        // Gọi trực tiếp productsScreen từ BaseTest, KHÔNG dùng từ khóa 'new'
        productsScreen
            .openMenuAndGoToLogin()
            .loginSuccess(LoginScreenModel.STANDARD_USER, LoginScreenModel.DEFAULT_PASSWORD)
            .verifyProductPageDisplayed()
            .openMenuAndLogout();
    }
}
```

---

## 🚀 Hướng dẫn chạy kiểm thử

### 1. Chuẩn bị môi trường
1. Bật máy ảo Android (Emulator) hoặc cắm thiết bị thật.
2. Bật Appium Server trong Terminal:
   ```bash
   appium
   ```

### 2. Chạy kiểm thử bằng Maven
```bash
# Chạy toàn bộ test suite
mvn clean test

# Mở báo cáo Allure Report sau khi chạy xong
allure serve target/allure-results
```
