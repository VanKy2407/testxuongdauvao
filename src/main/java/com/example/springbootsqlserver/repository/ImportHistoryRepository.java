package com.example.springbootsqlserver.repository;

import com.example.springbootsqlserver.entity.ImportHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImportHistoryRepository extends JpaRepository<ImportHistory, UUID> {

    Page<ImportHistory> findAllByOrderByImportDateDesc(Pageable pageable);
}
