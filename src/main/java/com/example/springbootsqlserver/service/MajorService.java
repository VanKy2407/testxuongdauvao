package com.example.springbootsqlserver.service;

import com.example.springbootsqlserver.entity.Major;
import com.example.springbootsqlserver.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MajorService {

    @Autowired
    private MajorRepository majorRepository;

    public List<Major> getAllMajors() {
        return majorRepository.findAll();
    }

    public Major getMajorById(UUID id) {
        return majorRepository.findById(id).orElse(null);
    }

    public Major createMajor(Major major) {
        return majorRepository.save(major);
    }

    public Major updateMajor(UUID id, Major major) {
        if (majorRepository.existsById(id)) {
            major.setId(id);
            return majorRepository.save(major);
        }
        return null;
    }

    public void deleteMajor(UUID id) {
        majorRepository.deleteById(id);
    }

    public Optional<Major> getMajorByCode(String code) {
        return majorRepository.findByCode(code);
    }

    public List<Major> getMajorsByDepartmentId(UUID departmentId) {
        return majorRepository.findByDepartmentId(departmentId);
    }
}
