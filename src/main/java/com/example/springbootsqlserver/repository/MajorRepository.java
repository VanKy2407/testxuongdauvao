package com.example.springbootsqlserver.repository;

import com.example.springbootsqlserver.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MajorRepository extends JpaRepository<Major, UUID> {

    Optional<Major> findByCode(String code);

    @Query("SELECT m FROM Major m JOIN MajorFacility mf ON m.id = mf.major.id "
            + "JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id "
            + "WHERE df.department.id = :departmentId")
    List<Major> findByDepartmentId(@Param("departmentId") UUID departmentId);
}
