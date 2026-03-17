package com.financetracker.repository;

import com.financetracker.model.Export;
import com.financetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportRepository extends JpaRepository<Export, Long> {
    List<Export> findByUserOrderByExportedAtDesc(User user);
}
