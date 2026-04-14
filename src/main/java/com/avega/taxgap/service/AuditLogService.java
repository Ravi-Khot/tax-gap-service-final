package com.avega.taxgap.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avega.taxgap.entity.AuditLog;
import com.avega.taxgap.enums.EventType;
import com.avega.taxgap.repository.AuditLogRepository;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    // Save audit log for tracking events
    public void log(EventType eventType, String transactionId, String detailJson) {
        AuditLog log = AuditLog.builder()
                .eventType(eventType)
                .transactionId(transactionId)
                .timestamp(LocalDateTime.now())
                .detailJson(detailJson)
                .build();

        auditLogRepository.save(log);
    }
}
