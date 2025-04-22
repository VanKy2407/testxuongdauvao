package com.example.springbootsqlserver.repository;

import com.example.springbootsqlserver.entity.DepartmentFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentFacilityRepository extends JpaRepository<DepartmentFacility, UUID> {

    List<DepartmentFacility> findByFacilityId(UUID facilityId);

    List<DepartmentFacility> findByDepartmentIdAndFacilityId(UUID departmentId, UUID facilityId);
}
