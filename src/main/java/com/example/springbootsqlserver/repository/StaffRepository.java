package com.example.springbootsqlserver.repository;

import com.example.springbootsqlserver.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    Staff findByAccountFe(String accountFe);

    Staff findByAccountFpt(String accountFpt);

    Staff findByStaffCode(String staffCode);

    Page<Staff> findByStatus(Byte status, Pageable pageable);

    @Query("SELECT s FROM Staff s WHERE "
            + "(LOWER(s.staffCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(s.accountFpt) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(s.accountFe) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND s.status = :status")
    Page<Staff> findByStaffCodeContainingOrNameContainingOrAccountFptContainingOrAccountFeContainingAndStatus(
            @Param("keyword") String keyword,
            @Param("keyword") String keyword2,
            @Param("keyword") String keyword3,
            @Param("keyword") String keyword4,
            @Param("status") Byte status,
            Pageable pageable);

    @Query("SELECT s FROM Staff s WHERE "
            + "LOWER(s.staffCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(s.accountFpt) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(s.accountFe) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Staff> findByStaffCodeContainingOrNameContainingOrAccountFptContainingOrAccountFeContaining(
            @Param("keyword") String keyword,
            @Param("keyword") String keyword2,
            @Param("keyword") String keyword3,
            @Param("keyword") String keyword4,
            Pageable pageable);
}
