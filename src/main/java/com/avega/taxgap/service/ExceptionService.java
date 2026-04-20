package com.avega.taxgap.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avega.taxgap.dto.CustomerExceptionCountDto;
import com.avega.taxgap.dto.ExceptionResponseDto;
import com.avega.taxgap.dto.ExceptionSummaryDto;
import com.avega.taxgap.dto.FullExceptionSummaryDto;
import com.avega.taxgap.dto.SeverityCountDto;
import com.avega.taxgap.entity.ExceptionRecord;
import com.avega.taxgap.enums.RuleType;
import com.avega.taxgap.enums.Severity;
import com.avega.taxgap.repository.ExceptionRecordRepository;


@Service
public class ExceptionService {

    @Autowired
    private ExceptionRecordRepository exceptionRecordRepository;

    // Create and save exception record
    public void createException(String transactionId,
                                String customerId,
                                RuleType ruleType,
                                Severity severity,
                                String message) {

        ExceptionRecord record = new ExceptionRecord();
        record.setTransactionId(transactionId);
        record.setCustomerId(customerId);
        record.setRuleName(ruleType.name());
        record.setSeverity(severity);
        record.setMessage(message);
        record.setTimestamp(LocalDateTime.now());

        exceptionRecordRepository.save(record);
    }

    // Fetch exceptions based on optional filters
    public List<ExceptionResponseDto> getExceptions(String customerId, String severity, String ruleName) {

        List<ExceptionRecord> records;

        if (customerId != null && severity != null) {
            records = exceptionRecordRepository.findByCustomerIdAndSeverity(
                    customerId,
                    Severity.valueOf(severity.toUpperCase())
            );
        } else if (customerId != null && ruleName != null) {
            records = exceptionRecordRepository.findByCustomerIdAndRuleName(customerId, ruleName);
        } else if (customerId != null) {
            records = exceptionRecordRepository.findByCustomerId(customerId);
        } else if (severity != null) {
            records = exceptionRecordRepository.findBySeverity(Severity.valueOf(severity.toUpperCase()));
        } else if (ruleName != null) {
            records = exceptionRecordRepository.findByRuleName(ruleName);
        } else {
            records = exceptionRecordRepository.findAll();
        }
        
        // Convert entity list to response DTO list
        return records.stream()
                .map(record -> ExceptionResponseDto.builder()
                        .transactionId(record.getTransactionId())
                        .customerId(record.getCustomerId())
                        .ruleName(record.getRuleName())
                        .severity(record.getSeverity())
                        .message(record.getMessage())
                        .timestamp(record.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }
    
    // Get exception summary grouped by rule name
    public List<ExceptionSummaryDto> getExceptionSummary() {

        List<Object[]> results = exceptionRecordRepository.getExceptionSummary();

        return results.stream()
                .map(obj -> ExceptionSummaryDto.builder()
                        .ruleName((String) obj[0])
                        .count((Long) obj[1])
                        .build())
                .toList();
    }
    
    // Get full summary including total, severity-wise, and customer-wise exception counts
    public FullExceptionSummaryDto getFullExceptionSummary() {

        long totalExceptions = exceptionRecordRepository.count();

        List<SeverityCountDto> severityCounts =
                exceptionRecordRepository.getExceptionCountBySeverity()
                        .stream()
                        .map(obj ->SeverityCountDto.builder()
                                .severity(obj[0].toString())
                                .count((Long) obj[1])
                                .build())
                        .toList();

        List<CustomerExceptionCountDto> customerCounts =
                exceptionRecordRepository.getCustomerWiseExceptionCount()
                        .stream()
                        .map(obj -> CustomerExceptionCountDto.builder()
                                .customerId((String) obj[0])
                                .count((Long) obj[1])
                                .build())
                        .toList();

        return FullExceptionSummaryDto.builder()
                .totalExceptions(totalExceptions)
                .countBySeverity(severityCounts)
                .customerWiseExceptionCount(customerCounts)
                .build();
    }
}


