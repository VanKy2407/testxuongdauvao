package com.example.springbootsqlserver.repository;

import com.example.springbootsqlserver.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    Department findByCode(String code);
}
