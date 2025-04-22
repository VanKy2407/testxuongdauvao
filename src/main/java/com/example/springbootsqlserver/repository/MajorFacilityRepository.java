package com.example.springbootsqlserver.repository;

import com.example.springbootsqlserver.entity.MajorFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MajorFacilityRepository extends JpaRepository<MajorFacility, UUID> {

    MajorFacility findByDepartmentFacilityIdAndMajorId(UUID departmentFacilityId, UUID majorId);

    List<MajorFacility> findByDepartmentFacilityId(UUID departmentFacilityId);

    List<MajorFacility> findByMajorId(UUID majorId);
}
