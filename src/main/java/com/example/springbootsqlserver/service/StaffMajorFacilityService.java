package com.example.springbootsqlserver.service;

import com.example.springbootsqlserver.entity.StaffMajorFacility;
import com.example.springbootsqlserver.repository.StaffMajorFacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StaffMajorFacilityService {

    @Autowired
    private StaffMajorFacilityRepository staffMajorFacilityRepository;

    public List<StaffMajorFacility> getAllStaffMajorFacilities() {
        return staffMajorFacilityRepository.findAll();
    }

    public StaffMajorFacility getStaffMajorFacilityById(UUID id) {
        return staffMajorFacilityRepository.findById(id).orElse(null);
    }

    public StaffMajorFacility createStaffMajorFacility(StaffMajorFacility staffMajorFacility) {
        return staffMajorFacilityRepository.save(staffMajorFacility);
    }

    public StaffMajorFacility updateStaffMajorFacility(UUID id, StaffMajorFacility staffMajorFacility) {
        if (staffMajorFacilityRepository.existsById(id)) {
            staffMajorFacility.setId(id);
            return staffMajorFacilityRepository.save(staffMajorFacility);
        }
        return null;
    }

    public void deleteStaffMajorFacility(UUID id) {
        staffMajorFacilityRepository.deleteById(id);
    }

    public StaffMajorFacility getStaffMajorFacilityByMajorFacilityIdAndStaffId(UUID majorFacilityId, UUID staffId) {
        return staffMajorFacilityRepository.findByMajorFacilityIdAndStaffId(majorFacilityId, staffId);
    }

    public List<StaffMajorFacility> getStaffMajorFacilitiesByStaffIdAndFacilityId(UUID staffId, UUID facilityId) {
        return staffMajorFacilityRepository.findByStaffIdAndMajorFacilityDepartmentFacilityFacilityId(staffId, facilityId);
    }

    public List<StaffMajorFacility> getStaffMajorFacilitiesByStaffId(UUID staffId) {
        return staffMajorFacilityRepository.findByStaffId(staffId);
    }
}
