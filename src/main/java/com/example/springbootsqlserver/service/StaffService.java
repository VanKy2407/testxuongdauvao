package com.example.springbootsqlserver.service;

import com.example.springbootsqlserver.entity.Staff;
import com.example.springbootsqlserver.entity.Major;
import com.example.springbootsqlserver.entity.MajorFacility;
import com.example.springbootsqlserver.entity.StaffMajorFacility;
import com.example.springbootsqlserver.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private MajorService majorService;

    @Autowired
    private MajorFacilityService majorFacilityService;

    @Autowired
    private StaffMajorFacilityService staffMajorFacilityService;

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Staff getStaffById(UUID id) {
        return staffRepository.findById(id).orElse(null);
    }

    public Staff createStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public Staff updateStaff(UUID id, Staff staff) {
        if (staffRepository.existsById(id)) {
            staff.setId(id);
            return staffRepository.save(staff);
        }
        return null;
    }

    public void deleteStaff(UUID id) {
        staffRepository.deleteById(id);
    }

    public Staff getStaffByAccountFe(String accountFe) {
        return staffRepository.findByAccountFe(accountFe);
    }

    public Staff getStaffByAccountFpt(String accountFpt) {
        return staffRepository.findByAccountFpt(accountFpt);
    }

    public Staff getStaffByStaffCode(String staffCode) {
        return staffRepository.findByStaffCode(staffCode);
    }

    public boolean existsByStaffCode(String staffCode) {
        return staffRepository.findByStaffCode(staffCode) != null;
    }

    public boolean existsByAccountFpt(String accountFpt) {
        return staffRepository.findByAccountFpt(accountFpt) != null;
    }

    public boolean existsByAccountFe(String accountFe) {
        return staffRepository.findByAccountFe(accountFe) != null;
    }

    public Page<Staff> getAllStaffsPaged(Pageable pageable) {
        return staffRepository.findAll(pageable);
    }

    public Page<Staff> searchStaffs(String keyword, Byte status, Pageable pageable) {
        if (status != null) {
            return staffRepository.findByStaffCodeContainingOrNameContainingOrAccountFptContainingOrAccountFeContainingAndStatus(
                    keyword, keyword, keyword, keyword, status, pageable);
        }
        return staffRepository.findByStaffCodeContainingOrNameContainingOrAccountFptContainingOrAccountFeContaining(
                keyword, keyword, keyword, keyword, pageable);
    }

    public Page<Staff> getStaffsByStatus(Byte status, Pageable pageable) {
        return staffRepository.findByStatus(status, pageable);
    }

    public void linkStaffToMajor(UUID staffId, String majorCode) {
        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff != null) {
            Optional<Major> majorOptional = majorService.getMajorByCode(majorCode);
            if (majorOptional.isPresent()) {
                Major major = majorOptional.get();
                List<MajorFacility> majorFacilities = major.getMajorFacilities();
                if (!majorFacilities.isEmpty()) {
                    MajorFacility majorFacility = majorFacilities.get(0);
                    StaffMajorFacility staffMajorFacility = new StaffMajorFacility();
                    staffMajorFacility.setId(UUID.randomUUID());
                    staffMajorFacility.setStaff(staff);
                    staffMajorFacility.setMajorFacility(majorFacility);
                    staffMajorFacility.setStatus((byte) 1);
                    staffMajorFacility.setCreatedDate(System.currentTimeMillis());
                    staffMajorFacility.setLastModifiedDate(System.currentTimeMillis());
                    staffMajorFacilityService.createStaffMajorFacility(staffMajorFacility);
                }
            }
        }
    }
}
