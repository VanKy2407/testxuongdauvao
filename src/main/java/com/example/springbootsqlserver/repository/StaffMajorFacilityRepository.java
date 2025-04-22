package com.example.springbootsqlserver.repository;

import com.example.springbootsqlserver.entity.StaffMajorFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffMajorFacilityRepository extends JpaRepository<StaffMajorFacility, UUID> {

    StaffMajorFacility findByMajorFacilityIdAndStaffId(UUID majorFacilityId, UUID staffId);

    List<StaffMajorFacility> findByStaffIdAndMajorFacilityDepartmentFacilityFacilityId(UUID staffId, UUID facilityId);

    List<StaffMajorFacility> findByStaffId(UUID staffId);
}
