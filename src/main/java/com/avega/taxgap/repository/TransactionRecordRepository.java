package com.avega.taxgap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avega.taxgap.entity.TransactionRecord;
import com.avega.taxgap.enums.TransactionType;
import com.avega.taxgap.enums.ValidationStatus;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    Optional<TransactionRecord> findByTransactionId(String transactionId);
    List<TransactionRecord> findByCustomerId(String customerId);
    List<TransactionRecord> findByCustomerIdAndValidationStatus(String customerId,ValidationStatus validationStatus);
    List<TransactionRecord> findByCustomerIdAndTransactionType(String customerId, TransactionType transactionType);
    List<TransactionRecord> findByCustomerIdAndTransactionTypeAndValidationStatus(
            String customerId,
            TransactionType transactionType,
            ValidationStatus validationStatus
    );
}
