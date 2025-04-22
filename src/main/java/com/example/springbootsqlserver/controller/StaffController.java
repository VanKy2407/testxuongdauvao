package com.example.springbootsqlserver.controller;

import com.example.springbootsqlserver.entity.Staff;
import com.example.springbootsqlserver.service.StaffService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping
    public String listStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Byte status,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Staff> staffPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Tìm kiếm theo keyword
            staffPage = staffService.searchStaffs(keyword, status, pageable);
        } else if (status != null) {
            // Lọc theo status
            staffPage = staffService.getStaffsByStatus(status, pageable);
        } else {
            // Lấy tất cả
            staffPage = staffService.getAllStaffsPaged(pageable);
        }

        model.addAttribute("staffs", staffPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("totalItems", staffPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "staff/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("staff", new Staff());
        return "staff/form";
    }

    @PostMapping("/save")
    public String saveStaff(@Validated @ModelAttribute Staff staff, BindingResult result, RedirectAttributes redirectAttributes) {
        // Kiểm tra các ràng buộc
        if (staff.getStaffCode() != null && staff.getStaffCode().length() > 15) {
            result.rejectValue("staffCode", "error.staff", "Mã nhân viên phải nhỏ hơn 15 ký tự");
        }

        if (staff.getName() != null && staff.getName().length() > 100) {
            result.rejectValue("name", "error.staff", "Tên nhân viên phải nhỏ hơn 100 ký tự");
        }

        // Kiểm tra email FPT
        if (staff.getAccountFpt() != null) {
            if (staff.getAccountFpt().length() > 100) {
                result.rejectValue("accountFpt", "error.staff", "Email FPT phải nhỏ hơn 100 ký tự");
            }
            if (!staff.getAccountFpt().endsWith("@fpt.edu.vn")) {
                result.rejectValue("accountFpt", "error.staff", "Email FPT phải kết thúc bằng @fpt.edu.vn");
            }
            if (staff.getAccountFpt().contains(" ")) {
                result.rejectValue("accountFpt", "error.staff", "Email FPT không được chứa khoảng trắng");
            }
        }

        // Kiểm tra email FE
        if (staff.getAccountFe() != null) {
            if (staff.getAccountFe().length() > 100) {
                result.rejectValue("accountFe", "error.staff", "Email FE phải nhỏ hơn 100 ký tự");
            }
            if (!staff.getAccountFe().endsWith("@fe.edu.vn")) {
                result.rejectValue("accountFe", "error.staff", "Email FE phải kết thúc bằng @fe.edu.vn");
            }
            if (staff.getAccountFe().contains(" ")) {
                result.rejectValue("accountFe", "error.staff", "Email FE không được chứa khoảng trắng");
            }
        }

        // Kiểm tra trùng lặp
        if (staff.getId() == null) {
            if (staffService.existsByStaffCode(staff.getStaffCode())) {
                result.rejectValue("staffCode", "error.staff", "Mã nhân viên đã tồn tại");
            }
            if (staffService.existsByAccountFpt(staff.getAccountFpt())) {
                result.rejectValue("accountFpt", "error.staff", "Email FPT đã tồn tại");
            }
            if (staffService.existsByAccountFe(staff.getAccountFe())) {
                result.rejectValue("accountFe", "error.staff", "Email FE đã tồn tại");
            }
        } else {
            Staff existingStaff = staffService.getStaffById(staff.getId());
            if (!existingStaff.getStaffCode().equals(staff.getStaffCode())
                    && staffService.existsByStaffCode(staff.getStaffCode())) {
                result.rejectValue("staffCode", "error.staff", "Mã nhân viên đã tồn tại");
            }
            if (!existingStaff.getAccountFpt().equals(staff.getAccountFpt())
                    && staffService.existsByAccountFpt(staff.getAccountFpt())) {
                result.rejectValue("accountFpt", "error.staff", "Email FPT đã tồn tại");
            }
            if (!existingStaff.getAccountFe().equals(staff.getAccountFe())
                    && staffService.existsByAccountFe(staff.getAccountFe())) {
                result.rejectValue("accountFe", "error.staff", "Email FE đã tồn tại");
            }
        }

        if (result.hasErrors()) {
            return "staff/form";
        }

        try {
            if (staff.getId() == null) {
                staffService.createStaff(staff);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm nhân viên thành công");
            } else {
                staffService.updateStaff(staff.getId(), staff);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhân viên thành công");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/staff";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Staff staff = staffService.getStaffById(id);
        if (staff != null) {
            model.addAttribute("staff", staff);
            return "staff/form";
        }
        return "redirect:/staff";
    }

    @GetMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            Staff staff = staffService.getStaffById(id);
            if (staff != null) {
                staff.setStatus(staff.getStatus() == 1 ? (byte) 0 : (byte) 1);
                staffService.updateStaff(id, staff);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã thay đổi trạng thái nhân viên thành "
                        + (staff.getStatus() == 1 ? "Đang hoạt động" : "Ngừng hoạt động"));
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi thay đổi trạng thái");
        }
        return "redirect:/staff";
    }

    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadEmptyTemplate() throws IOException {
        return generateExcelTemplate(null);
    }

    @PostMapping("/download-template")
    public ResponseEntity<byte[]> downloadTemplateWithData(@RequestParam("selectedStaffData") String selectedStaffData) throws IOException {
        System.out.println("Received data: " + selectedStaffData);

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> staffDataList;

        try {
            staffDataList = mapper.readValue(selectedStaffData, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            System.err.println("Error parsing JSON data: " + e.getMessage());
            return downloadEmptyTemplate();
        }

        if (staffDataList == null || staffDataList.isEmpty()) {
            System.out.println("No data received, returning empty template");
            return downloadEmptyTemplate();
        }

        List<Staff> selectedStaff = staffDataList.stream()
                .map(staffMap -> {

                    Staff staff = new Staff();
                    try {
                        String id = String.valueOf(staffMap.get("id"));
                        System.out.println("Processing staff with ID: " + id);

                        staff.setId(UUID.fromString(id));
                        staff.setStaffCode(String.valueOf(staffMap.get("staffCode")));
                        staff.setName(String.valueOf(staffMap.get("name")));
                        staff.setAccountFpt(String.valueOf(staffMap.get("accountFpt")));
                        staff.setAccountFe(String.valueOf(staffMap.get("accountFe")));

                        Object statusObj = staffMap.get("status");
                        byte status;
                        if (statusObj instanceof Number) {
                            status = ((Number) statusObj).byteValue();
                        } else if (statusObj instanceof String) {
                            status = Byte.parseByte((String) statusObj);
                        } else {
                            status = 0;
                        }
                        staff.setStatus(status);

                        System.out.println("Successfully processed staff: " + staff.getStaffCode());
                        return staff;
                    } catch (Exception e) {
                        System.err.println("Error processing staff data: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(staff -> staff != null && staff.getId() != null)
                .collect(Collectors.toList());

        System.out.println("Final staff list size: " + selectedStaff.size());
        return generateExcelTemplate(selectedStaff);
    }

    private ResponseEntity<byte[]> generateExcelTemplate(List<Staff> staffList) throws IOException {
        System.out.println("Generating template for staff: " + staffList); // Log để debug

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Staff Template");

            // Tạo font cho header
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Tạo style cho data
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.LEFT);

            // Tạo style cho note
            CellStyle noteStyle = workbook.createCellStyle();
            Font noteFont = workbook.createFont();
            noteFont.setColor(IndexedColors.RED.getIndex());
            noteStyle.setFont(noteFont);

            // Tạo hàng header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Mã nhân viên (*)", "Tên nhân viên (*)", "Email FPT (*)", "Email FE (*)", "Trạng thái (*)"};

            // Thiết lập header
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 6000);
            }

            // Thêm dữ liệu nếu có
            if (staffList != null && !staffList.isEmpty()) {
                System.out.println("Adding " + staffList.size() + " staff records"); // Log để debug
                int rowNum = 1;
                for (Staff staff : staffList) {
                    if (staff != null) {
                        Row row = sheet.createRow(rowNum++);

                        Cell codeCell = row.createCell(0);
                        codeCell.setCellValue(staff.getStaffCode() != null ? staff.getStaffCode() : "");
                        codeCell.setCellStyle(dataStyle);

                        Cell nameCell = row.createCell(1);
                        nameCell.setCellValue(staff.getName() != null ? staff.getName() : "");
                        nameCell.setCellStyle(dataStyle);

                        Cell fptCell = row.createCell(2);
                        fptCell.setCellValue(staff.getAccountFpt() != null ? staff.getAccountFpt() : "");
                        fptCell.setCellStyle(dataStyle);

                        Cell feCell = row.createCell(3);
                        feCell.setCellValue(staff.getAccountFe() != null ? staff.getAccountFe() : "");
                        feCell.setCellStyle(dataStyle);

                        Cell statusCell = row.createCell(4);
                        statusCell.setCellValue(staff.getStatus());
                        statusCell.setCellStyle(dataStyle);

                        System.out.println("Added row " + rowNum + ": " + staff.getStaffCode()); // Log để debug
                    }
                }
            }

            // Thêm ghi chú về định dạng
            int noteStartRow = staffList != null && !staffList.isEmpty() ? sheet.getLastRowNum() + 2 : 2;

            Row noteRow1 = sheet.createRow(noteStartRow);
            Cell noteCell1 = noteRow1.createCell(0);
            noteCell1.setCellValue("Ghi chú:");
            noteCell1.setCellStyle(noteStyle);

            Row noteRow2 = sheet.createRow(noteStartRow + 1);
            Cell noteCell2 = noteRow2.createCell(0);
            noteCell2.setCellValue("- Các cột đánh dấu (*) là bắt buộc");
            noteCell2.setCellStyle(noteStyle);

            Row noteRow3 = sheet.createRow(noteStartRow + 2);
            Cell noteCell3 = noteRow3.createCell(0);
            noteCell3.setCellValue("- Mã nhân viên: tối đa 15 ký tự");
            noteCell3.setCellStyle(noteStyle);

            Row noteRow4 = sheet.createRow(noteStartRow + 3);
            Cell noteCell4 = noteRow4.createCell(0);
            noteCell4.setCellValue("- Tên nhân viên: tối đa 100 ký tự");
            noteCell4.setCellStyle(noteStyle);

            Row noteRow5 = sheet.createRow(noteStartRow + 4);
            Cell noteCell5 = noteRow5.createCell(0);
            noteCell5.setCellValue("- Email FPT phải kết thúc bằng @fpt.edu.vn");
            noteCell5.setCellStyle(noteStyle);

            Row noteRow6 = sheet.createRow(noteStartRow + 5);
            Cell noteCell6 = noteRow6.createCell(0);
            noteCell6.setCellValue("- Email FE phải kết thúc bằng @fe.edu.vn");
            noteCell6.setCellStyle(noteStyle);

            Row noteRow7 = sheet.createRow(noteStartRow + 6);
            Cell noteCell7 = noteRow7.createCell(0);
            noteCell7.setCellValue("- Trạng thái: 1 (Đang hoạt động) hoặc 0 (Ngừng hoạt động)");
            noteCell7.setCellStyle(noteStyle);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "staff_template.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        }
    }

    @PostMapping("/import")
    @ResponseBody
    public Map<String, Object> importStaff(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        List<String> errors = new ArrayList<>();

        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Bỏ qua header
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    String staffCode = getCellValue(row.getCell(0));
                    String name = getCellValue(row.getCell(1));
                    String accountFpt = getCellValue(row.getCell(2));
                    String accountFe = getCellValue(row.getCell(3));
                    String statusStr = getCellValue(row.getCell(4));
                    String majorCode = getCellValue(row.getCell(5)); // Thêm cột bộ môn chuyên ngành

                    // Validate dữ liệu
                    if (staffCode == null || staffCode.trim().isEmpty()) {
                        errors.add("Dòng " + (row.getRowNum() + 1) + ": Mã nhân viên không được để trống");
                        continue;
                    }

                    if (name == null || name.trim().isEmpty()) {
                        errors.add("Dòng " + (row.getRowNum() + 1) + ": Tên nhân viên không được để trống");
                        continue;
                    }

                    // Validate email FPT
                    if (accountFpt != null && !accountFpt.trim().isEmpty()) {
                        if (!accountFpt.endsWith("@fpt.edu.vn")) {
                            errors.add("Dòng " + (row.getRowNum() + 1) + ": Email FPT phải kết thúc bằng @fpt.edu.vn");
                            continue;
                        }
                        if (!accountFpt.contains(staffCode)) {
                            errors.add("Dòng " + (row.getRowNum() + 1) + ": Email FPT phải chứa mã nhân viên");
                            continue;
                        }
                    }

                    // Validate email FE
                    if (accountFe != null && !accountFe.trim().isEmpty()) {
                        if (!accountFe.endsWith("@fe.edu.vn")) {
                            errors.add("Dòng " + (row.getRowNum() + 1) + ": Email FE phải kết thúc bằng @fe.edu.vn");
                            continue;
                        }
                        if (!accountFe.contains(staffCode)) {
                            errors.add("Dòng " + (row.getRowNum() + 1) + ": Email FE phải chứa mã nhân viên");
                            continue;
                        }
                    }

                    // Validate bộ môn chuyên ngành
                    if (majorCode == null || majorCode.trim().isEmpty()) {
                        errors.add("Dòng " + (row.getRowNum() + 1) + ": Mã bộ môn chuyên ngành không được để trống");
                        continue;
                    }

                    byte status = 0;
                    if (statusStr != null && !statusStr.trim().isEmpty()) {
                        try {
                            status = Byte.parseByte(statusStr);
                        } catch (NumberFormatException e) {
                            errors.add("Dòng " + (row.getRowNum() + 1) + ": Trạng thái phải là 0 hoặc 1");
                            continue;
                        }
                    }

                    // Kiểm tra trùng lặp
                    if (staffService.existsByStaffCode(staffCode)) {
                        errors.add("Dòng " + (row.getRowNum() + 1) + ": Mã nhân viên đã tồn tại");
                        continue;
                    }

                    if (accountFpt != null && !accountFpt.trim().isEmpty() && staffService.existsByAccountFpt(accountFpt)) {
                        errors.add("Dòng " + (row.getRowNum() + 1) + ": Email FPT đã tồn tại");
                        continue;
                    }

                    if (accountFe != null && !accountFe.trim().isEmpty() && staffService.existsByAccountFe(accountFe)) {
                        errors.add("Dòng " + (row.getRowNum() + 1) + ": Email FE đã tồn tại");
                        continue;
                    }

                    // Tạo và lưu nhân viên
                    Staff staff = new Staff();
                    staff.setStaffCode(staffCode);
                    staff.setName(name);
                    staff.setAccountFpt(accountFpt);
                    staff.setAccountFe(accountFe);
                    staff.setStatus(status);

                    // Lưu nhân viên
                    Staff savedStaff = staffService.createStaff(staff);

                    // Liên kết với bộ môn chuyên ngành
                    if (savedStaff != null && majorCode != null && !majorCode.trim().isEmpty()) {
                        staffService.linkStaffToMajor(savedStaff.getId(), majorCode);
                    }

                } catch (Exception e) {
                    errors.add("Dòng " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }

            if (errors.isEmpty()) {
                response.put("success", true);
                response.put("message", "Import dữ liệu thành công");
            } else {
                response.put("success", false);
                response.put("message", "Có lỗi xảy ra khi import dữ liệu");
                response.put("errors", errors);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi đọc file: " + e.getMessage());
        }

        return response;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
}
