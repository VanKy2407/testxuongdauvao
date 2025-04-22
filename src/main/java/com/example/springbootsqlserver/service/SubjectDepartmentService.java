package com.example.springbootsqlserver.service;

import com.example.springbootsqlserver.entity.SubjectDepartment;
import com.example.springbootsqlserver.repository.SubjectDepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubjectDepartmentService {

    @Autowired
    private SubjectDepartmentRepository subjectDepartmentRepository;

    public List<SubjectDepartment> getAllSubjectDepartments() {
        return subjectDepartmentRepository.findAll();
    }

    public Optional<SubjectDepartment> getSubjectDepartmentById(UUID id) {
        return subjectDepartmentRepository.findById(id);
    }

    public Optional<SubjectDepartment> getSubjectDepartmentByCode(String code) {
        return subjectDepartmentRepository.findByCode(code);
    }

    public List<SubjectDepartment> getSubjectDepartmentsByDepartmentId(UUID departmentId) {
        return subjectDepartmentRepository.findByDepartmentId(departmentId);
    }

    public SubjectDepartment createSubjectDepartment(SubjectDepartment subjectDepartment) {
        if (subjectDepartmentRepository.existsByCode(subjectDepartment.getCode())) {
            throw new RuntimeException("Subject department with code " + subjectDepartment.getCode() + " already exists");
        }
        return subjectDepartmentRepository.save(subjectDepartment);
    }

    public SubjectDepartment updateSubjectDepartment(UUID id, SubjectDepartment subjectDepartment) {
        if (!subjectDepartmentRepository.existsById(id)) {
            throw new RuntimeException("Subject department not found with id: " + id);
        }
        subjectDepartment.setId(id);
        return subjectDepartmentRepository.save(subjectDepartment);
    }

    public void deleteSubjectDepartment(UUID id) {
        if (!subjectDepartmentRepository.existsById(id)) {
            throw new RuntimeException("Subject department not found with id: " + id);
        }
        subjectDepartmentRepository.deleteById(id);
    }
}
