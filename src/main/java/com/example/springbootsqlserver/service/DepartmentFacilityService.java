package com.example.springbootsqlserver.service;

import com.example.springbootsqlserver.entity.DepartmentFacility;
import com.example.springbootsqlserver.repository.DepartmentFacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentFacilityService {

    @Autowired
    private DepartmentFacilityRepository departmentFacilityRepository;

    public List<DepartmentFacility> getAllDepartmentFacilities() {
        return departmentFacilityRepository.findAll();
    }

    public DepartmentFacility getDepartmentFacilityById(UUID id) {
        return departmentFacilityRepository.findById(id).orElse(null);
    }

    public DepartmentFacility createDepartmentFacility(DepartmentFacility departmentFacility) {
        return departmentFacilityRepository.save(departmentFacility);
    }

    public DepartmentFacility updateDepartmentFacility(UUID id, DepartmentFacility departmentFacility) {
        if (departmentFacilityRepository.existsById(id)) {
            departmentFacility.setId(id);
            return departmentFacilityRepository.save(departmentFacility);
        }
        return null;
    }

    public void deleteDepartmentFacility(UUID id) {
        departmentFacilityRepository.deleteById(id);
    }

    public DepartmentFacility getDepartmentFacilityByDepartmentIdAndFacilityId(UUID departmentId, UUID facilityId) {
        List<DepartmentFacility> departmentFacilities = departmentFacilityRepository.findByDepartmentIdAndFacilityId(departmentId, facilityId);
        return departmentFacilities.isEmpty() ? null : departmentFacilities.get(0);
    }

    public List<DepartmentFacility> getDepartmentFacilitiesByFacilityId(UUID facilityId) {
        return departmentFacilityRepository.findByFacilityId(facilityId);
    }

    public List<DepartmentFacility> getDepartmentFacilitiesByDepartmentIdAndFacilityId(UUID departmentId, UUID facilityId) {
        return departmentFacilityRepository.findByDepartmentIdAndFacilityId(departmentId, facilityId);
    }
}
