package com.example.springbootsqlserver.service;

import com.example.springbootsqlserver.entity.MajorFacility;
import com.example.springbootsqlserver.repository.MajorFacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MajorFacilityService {

    @Autowired
    private MajorFacilityRepository majorFacilityRepository;

    public List<MajorFacility> getAllMajorFacilities() {
        return majorFacilityRepository.findAll();
    }

    public MajorFacility getMajorFacilityById(UUID id) {
        return majorFacilityRepository.findById(id).orElse(null);
    }

    public MajorFacility createMajorFacility(MajorFacility majorFacility) {
        return majorFacilityRepository.save(majorFacility);
    }

    public MajorFacility updateMajorFacility(UUID id, MajorFacility majorFacility) {
        if (majorFacilityRepository.existsById(id)) {
            majorFacility.setId(id);
            return majorFacilityRepository.save(majorFacility);
        }
        return null;
    }

    public void deleteMajorFacility(UUID id) {
        majorFacilityRepository.deleteById(id);
    }

    public MajorFacility getMajorFacilityByDepartmentFacilityIdAndMajorId(UUID departmentFacilityId, UUID majorId) {
        return majorFacilityRepository.findByDepartmentFacilityIdAndMajorId(departmentFacilityId, majorId);
    }

    public List<MajorFacility> getMajorFacilitiesByDepartmentFacilityId(UUID departmentFacilityId) {
        return majorFacilityRepository.findByDepartmentFacilityId(departmentFacilityId);
    }

    public List<MajorFacility> getMajorFacilitiesByMajorId(UUID majorId) {
        return majorFacilityRepository.findByMajorId(majorId);
    }
}
