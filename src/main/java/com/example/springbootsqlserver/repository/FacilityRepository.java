package com.example.springbootsqlserver.repository;

import com.example.springbootsqlserver.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, UUID> {

    Facility findByCode(String code);
}
