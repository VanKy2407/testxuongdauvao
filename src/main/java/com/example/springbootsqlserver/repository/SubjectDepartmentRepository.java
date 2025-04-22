package com.example.springbootsqlserver.repository;

import com.example.springbootsqlserver.entity.SubjectDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectDepartmentRepository extends JpaRepository<SubjectDepartment, UUID> {

    Optional<SubjectDepartment> findByCode(String code);

    List<SubjectDepartment> findByDepartmentId(UUID departmentId);

    boolean existsByCode(String code);
}
