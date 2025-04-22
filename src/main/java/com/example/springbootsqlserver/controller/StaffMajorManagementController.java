package com.example.springbootsqlserver.controller;

import com.example.springbootsqlserver.entity.*;
import com.example.springbootsqlserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-major-management")
public class StaffMajorManagementController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentFacilityService departmentFacilityService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private MajorFacilityService majorFacilityService;

    @Autowired
    private StaffMajorFacilityService staffMajorFacilityService;

    @Autowired
    private SubjectDepartmentService subjectDepartmentService;

    @GetMapping
    public String showManagementPage(@RequestParam(required = false) UUID staffId, Model model) {
        List<Staff> staffList = staffService.getAllStaff();
        List<Facility> facilities = facilityService.getAllFacilities();
        List<Department> department = departmentService.getAllDepartments();
        List<Major> major = majorService.getAllMajors();
        model.addAttribute("staffList", staffList);
        model.addAttribute("facilities", facilities);
        model.addAttribute("major", major);
        model.addAttribute("department", department);

        if (staffId != null) {
            Staff selectedStaff = staffService.getStaffById(staffId);
            model.addAttribute("selectedStaff", selectedStaff);
        }

        return "staff-major-management";
    }

    @GetMapping("/subjects/facility/{facilityId}")
    @ResponseBody
    public ResponseEntity<List<SubjectDepartment>> getSubjectsByFacility(@PathVariable UUID facilityId) {
        // Lấy tất cả bộ môn và lọc theo facility
        List<SubjectDepartment> allSubjects = subjectDepartmentService.getAllSubjectDepartments();
        List<SubjectDepartment> filteredSubjects = new ArrayList<>();

        for (SubjectDepartment subject : allSubjects) {
            // TODO: Implement logic to filter subjects by facility
            filteredSubjects.add(subject);
        }

        return ResponseEntity.ok(filteredSubjects);
    }

    @GetMapping("/majors/subject/{subjectId}")
    @ResponseBody
    public ResponseEntity<List<Major>> getMajorsBySubjectDepartment(@PathVariable UUID subjectId) {
        // Lấy tất cả chuyên ngành
        List<Major> allMajors = majorService.getAllMajors();
        List<Major> filteredMajors = new ArrayList<>();

        // TODO: Implement logic to filter majors by subject
        filteredMajors.addAll(allMajors);

        return ResponseEntity.ok(filteredMajors);
    }

    @GetMapping("/departments/{facilityId}")
    @ResponseBody
    public ResponseEntity<List<Department>> getDepartmentsByFacility(@PathVariable UUID facilityId) {
        List<DepartmentFacility> departmentFacilities = departmentFacilityService.getDepartmentFacilitiesByFacilityId(facilityId);
        List<Department> departments = new ArrayList<>();
        for (DepartmentFacility df : departmentFacilities) {
            Department department = df.getDepartment();
            if (department != null) {
                departments.add(department);
            }
        }
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/majors/{departmentId}/{facilityId}")
    @ResponseBody
    public ResponseEntity<List<MajorFacility>> getMajorsByDepartmentAndFacility(
            @PathVariable UUID departmentId,
            @PathVariable UUID facilityId) {

        // Lấy DepartmentFacility dựa trên departmentId và facilityId
        List<DepartmentFacility> departmentFacilities = departmentFacilityService
                .getDepartmentFacilitiesByDepartmentIdAndFacilityId(departmentId, facilityId);

        if (departmentFacilities.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        DepartmentFacility departmentFacility = departmentFacilities.get(0);
        List<MajorFacility> majorFacilities = majorFacilityService
                .getMajorFacilitiesByDepartmentFacilityId(departmentFacility.getId());

        return ResponseEntity.ok(majorFacilities);
    }

    @PostMapping("/assign")
    @ResponseBody
    public ResponseEntity<?> assignStaffToMajor(
            @RequestParam(required = true) UUID staffId,
            @RequestParam(required = true) UUID majorFacilityId) {

        try {
            System.out.println("Received staffId: " + staffId);
            System.out.println("Received majorFacilityId: " + majorFacilityId);

            // Kiểm tra xem nhân viên đã được phân công cho bộ môn nào trong cơ sở này chưa
            MajorFacility majorFacility = majorFacilityService.getMajorFacilityById(majorFacilityId);
            if (majorFacility == null) {
                return ResponseEntity.badRequest()
                        .body("Không tìm thấy thông tin bộ môn chuyên ngành");
            }

            Staff staff = staffService.getStaffById(staffId);
            if (staff == null) {
                return ResponseEntity.badRequest()
                        .body("Không tìm thấy thông tin nhân viên");
            }

            StaffMajorFacility staffMajorFacility = new StaffMajorFacility();
            staffMajorFacility.setStaff(staff);
            staffMajorFacility.setMajorFacility(majorFacility);
            staffMajorFacility.setStatus((byte) 1);
            staffMajorFacility.setCreatedDate(System.currentTimeMillis());
            staffMajorFacility.setLastModifiedDate(System.currentTimeMillis());

            staffMajorFacilityService.createStaffMajorFacility(staffMajorFacility);

            return ResponseEntity.ok()
                    .body("Phân công nhân viên thành công");
        } catch (Exception e) {
            System.out.println("Error in assignStaffToMajor: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @DeleteMapping("/{staffMajorFacilityId}")
    @ResponseBody
    public ResponseEntity<?> removeStaffFromMajor(@PathVariable UUID staffMajorFacilityId) {
        staffMajorFacilityService.deleteStaffMajorFacility(staffMajorFacilityId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/staff-assignments/{staffId}")
    @ResponseBody
    public ResponseEntity<List<StaffMajorFacility>> getStaffAssignments(@PathVariable UUID staffId) {
        List<StaffMajorFacility> assignments = staffMajorFacilityService.getStaffMajorFacilitiesByStaffId(staffId);
        return ResponseEntity.ok(assignments);
    }
}
