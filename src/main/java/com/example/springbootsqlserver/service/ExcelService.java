package com.example.springbootsqlserver.service;

import com.example.springbootsqlserver.entity.*;
import com.example.springbootsqlserver.repository.ImportHistoryRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ExcelService {

    @Autowired
    private StaffService staffService;

    @Autowired
    private MajorFacilityService majorFacilityService;

    @Autowired
    private StaffMajorFacilityService staffMajorFacilityService;

    @Autowired
    private ImportHistoryRepository importHistoryRepository;

    public ImportHistory importStaffFromExcel(MultipartFile file, Staff importedBy) {
        ImportHistory history = new ImportHistory();
        history.setFileName(file.getOriginalFilename());
        history.setImportDate(System.currentTimeMillis());
        history.setImportedBy(importedBy);

        List<String> importDetails = new ArrayList<>();
        int totalRecords = 0;
        int successRecords = 0;
        int failedRecords = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Bỏ qua hàng header
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                totalRecords++;

                try {
                    // Kiểm tra xem hàng có phải là ghi chú không
                    String firstCell = getCellValueAsString(row.getCell(0));
                    if (firstCell != null && firstCell.startsWith("Ghi chú:")) {
                        break;
                    }

                    // Đọc dữ liệu từ Excel
                    String staffCode = getCellValueAsString(row.getCell(0));
                    String name = getCellValueAsString(row.getCell(1));
                    String accountFpt = getCellValueAsString(row.getCell(2));
                    String accountFe = getCellValueAsString(row.getCell(3));
                    Byte status = Byte.parseByte(getCellValueAsString(row.getCell(4)));
                    String majorFacilityId = getCellValueAsString(row.getCell(5));

                    // Validate dữ liệu
                    List<String> errors = validateStaffData(staffCode, name, accountFpt, accountFe, status);
                    if (!errors.isEmpty()) {
                        importDetails.add(String.format("Dòng %d: %s - Lỗi: %s",
                                row.getRowNum() + 1, staffCode, String.join(", ", errors)));
                        failedRecords++;
                        continue;
                    }

                    // Kiểm tra trùng lặp
                    if (staffService.existsByStaffCode(staffCode)) {
                        importDetails.add(String.format("Dòng %d: %s - Lỗi: Mã nhân viên đã tồn tại",
                                row.getRowNum() + 1, staffCode));
                        failedRecords++;
                        continue;
                    }

                    if (staffService.existsByAccountFpt(accountFpt)) {
                        importDetails.add(String.format("Dòng %d: %s - Lỗi: Email FPT đã tồn tại",
                                row.getRowNum() + 1, staffCode));
                        failedRecords++;
                        continue;
                    }

                    if (staffService.existsByAccountFe(accountFe)) {
                        importDetails.add(String.format("Dòng %d: %s - Lỗi: Email FE đã tồn tại",
                                row.getRowNum() + 1, staffCode));
                        failedRecords++;
                        continue;
                    }

                    // Tạo nhân viên mới
                    Staff staff = new Staff();
                    staff.setStaffCode(staffCode);
                    staff.setName(name);
                    staff.setAccountFpt(accountFpt);
                    staff.setAccountFe(accountFe);
                    staff.setStatus(status);
                    staff = staffService.createStaff(staff);

                    // Nếu có majorFacilityId, tạo liên kết với bộ môn chuyên ngành
                    if (majorFacilityId != null && !majorFacilityId.trim().isEmpty()) {
                        MajorFacility majorFacility = majorFacilityService.getMajorFacilityById(UUID.fromString(majorFacilityId));
                        if (majorFacility != null) {
                            StaffMajorFacility staffMajorFacility = new StaffMajorFacility();
                            staffMajorFacility.setStaff(staff);
                            staffMajorFacility.setMajorFacility(majorFacility);
                            staffMajorFacility.setStatus((byte) 1);
                            staffMajorFacilityService.createStaffMajorFacility(staffMajorFacility);
                        }
                    }

                    importDetails.add(String.format("Dòng %d: %s - Import thành công",
                            row.getRowNum() + 1, staffCode));
                    successRecords++;

                } catch (Exception e) {
                    importDetails.add(String.format("Dòng %d: Lỗi xử lý - %s",
                            row.getRowNum() + 1, e.getMessage()));
                    failedRecords++;
                }
            }

        } catch (IOException e) {
            importDetails.add("Lỗi đọc file: " + e.getMessage());
        }

        history.setTotalRecords(totalRecords);
        history.setSuccessRecords(successRecords);
        history.setFailedRecords(failedRecords);
        history.setImportDetails(String.join("\n", importDetails));

        return importHistoryRepository.save(history);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private List<String> validateStaffData(String staffCode, String name, String accountFpt,
            String accountFe, Byte status) {
        List<String> errors = new ArrayList<>();

        if (staffCode == null || staffCode.trim().isEmpty()) {
            errors.add("Mã nhân viên không được để trống");
        } else if (staffCode.length() > 15) {
            errors.add("Mã nhân viên không được vượt quá 15 ký tự");
        }

        if (name == null || name.trim().isEmpty()) {
            errors.add("Tên nhân viên không được để trống");
        } else if (name.length() > 100) {
            errors.add("Tên nhân viên không được vượt quá 100 ký tự");
        }

        if (accountFpt == null || accountFpt.trim().isEmpty()) {
            errors.add("Email FPT không được để trống");
        } else if (!accountFpt.endsWith("@fpt.edu.vn")) {
            errors.add("Email FPT phải kết thúc bằng @fpt.edu.vn");
        } else if (accountFpt.length() > 100) {
            errors.add("Email FPT không được vượt quá 100 ký tự");
        }

        if (accountFe == null || accountFe.trim().isEmpty()) {
            errors.add("Email FE không được để trống");
        } else if (!accountFe.endsWith("@fe.edu.vn")) {
            errors.add("Email FE phải kết thúc bằng @fe.edu.vn");
        } else if (accountFe.length() > 100) {
            errors.add("Email FE không được vượt quá 100 ký tự");
        }

        if (status == null || (status != 0 && status != 1)) {
            errors.add("Trạng thái không hợp lệ (0: Ngừng hoạt động, 1: Đang hoạt động)");
        }

        return errors;
    }
}
