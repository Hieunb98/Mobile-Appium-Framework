package com.appium.helpers;

import com.poiji.bind.Poiji;
import com.poiji.option.PoijiOptions;
import com.appium.exceptions.InvalidPathForExcelException;

import java.io.File;
import java.util.List;

/**
 * ExcelHelpers - Tiện ích đọc dữ liệu Excel hiện đại thông qua thư viện Poiji.
 * Map trực tiếp các dòng trong Excel thành Java Object (POJO/DTO) type-safe chỉ bằng 1 dòng code.
 * Loại bỏ hoàn toàn Apache POI thủ công, không memory leak, thread-safe tuyệt đối khi chạy song song.
 */
public final class ExcelHelpers {

    private ExcelHelpers() {}

    /**
     * Đọc dữ liệu từ file Excel và ánh xạ trực tiếp sang danh sách POJO (sheet mặc định đầu tiên).
     *
     * @param filePath   Đường dẫn tới file Excel (.xlsx / .xls)
     * @param modelClass Lớp DTO cần map (chứa annotation @ExcelCellName)
     * @param <T>        Kiểu dữ liệu Generic của DTO
     * @return List<T> danh sách đối tượng POJO đã map
     */
    public static <T> List<T> getDataList(String filePath, Class<T> modelClass) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new InvalidPathForExcelException("File Excel path not found: " + filePath);
        }
        return Poiji.fromExcel(file, modelClass);
    }

    /**
     * Đọc dữ liệu từ một Sheet chỉ định trong file Excel và ánh xạ sang danh sách POJO.
     *
     * @param filePath   Đường dẫn tới file Excel (.xlsx / .xls)
     * @param sheetName  Tên Sheet cần đọc
     * @param modelClass Lớp DTO cần map (chứa annotation @ExcelCellName)
     * @param <T>        Kiểu dữ liệu Generic của DTO
     * @return List<T> danh sách đối tượng POJO đã map
     */
    public static <T> List<T> getDataList(String filePath, String sheetName, Class<T> modelClass) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new InvalidPathForExcelException("File Excel path not found: " + filePath);
        }
        PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();
        return Poiji.fromExcel(file, modelClass, options);
    }
}
