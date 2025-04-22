package com.example.springbootsqlserver.service;

import com.example.springbootsqlserver.entity.Facility;
import com.example.springbootsqlserver.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public Facility getFacilityById(UUID id) {
        return facilityRepository.findById(id).orElse(null);
    }

    public Facility createFacility(Facility facility) {
        return facilityRepository.save(facility);
    }

    public Facility updateFacility(UUID id, Facility facility) {
        if (facilityRepository.existsById(id)) {
            facility.setId(id);
            return facilityRepository.save(facility);
        }
        return null;
    }

    public void deleteFacility(UUID id) {
        facilityRepository.deleteById(id);
    }

    public Facility getFacilityByCode(String code) {
        return facilityRepository.findByCode(code);
    }
}
