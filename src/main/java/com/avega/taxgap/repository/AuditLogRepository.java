package com.avega.taxgap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avega.taxgap.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
