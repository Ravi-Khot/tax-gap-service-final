package com.avega.taxgap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avega.taxgap.entity.ExceptionRecord;
import com.avega.taxgap.enums.Severity;

public interface ExceptionRecordRepository extends JpaRepository<ExceptionRecord, Long> {
	

    List<ExceptionRecord> findByCustomerId(String customerId);
    
    List<ExceptionRecord> findBySeverity(Severity severity);
    
    List<ExceptionRecord> findByRuleName(String ruleName);
    
    List<ExceptionRecord> findByCustomerIdAndSeverity(String customerId, Severity severity);
    
    List<ExceptionRecord> findByCustomerIdAndRuleName(String customerId, String ruleName);
    
    // Summary: count of exceptions grouped by rule
    @Query("SELECT e.ruleName, COUNT(e) FROM ExceptionRecord e GROUP BY e.ruleName")
    List<Object[]> getExceptionSummary();
    
    // Summary: count grouped by severity
    @Query("SELECT e.severity, COUNT(e) FROM ExceptionRecord e GROUP BY e.severity")
    List<Object[]> getExceptionCountBySeverity();

    // Summary: count grouped by customer
    @Query("SELECT e.customerId, COUNT(e) FROM ExceptionRecord e GROUP BY e.customerId")
    List<Object[]> getCustomerWiseExceptionCount();
}
