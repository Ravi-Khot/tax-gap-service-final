package com.avega.taxgap.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avega.taxgap.dto.TransactionResultDto;
import com.avega.taxgap.dto.TransactionUploadRequest;
import com.avega.taxgap.dto.TransactionUploadResponse;
import com.avega.taxgap.entity.TransactionRecord;
import com.avega.taxgap.enums.ComplianceStatus;
import com.avega.taxgap.enums.EventType;
import com.avega.taxgap.enums.TransactionType;
import com.avega.taxgap.enums.ValidationStatus;
import com.avega.taxgap.repository.TransactionRecordRepository;
import com.avega.taxgap.util.JsonUtil;


@Service
public class TransactionService {

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private TaxComputationService taxComputationService;
    
    @Autowired
    private RuleEngineService ruleEngineService;

    // Process batch of transactions
    public TransactionUploadResponse processTransactions(List<TransactionUploadRequest> requests) {

        List<TransactionResultDto> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (TransactionUploadRequest req : requests) {

            List<String> failureReasons = new ArrayList<>();
            TransactionRecord record = new TransactionRecord();
            TransactionResultDto result = new TransactionResultDto();

            result.setTransactionId(req.getTransactionId());

         // set transactionId ONLY if valid
            if (req.getTransactionId() != null && !req.getTransactionId().trim().isEmpty()) {
                record.setTransactionId(req.getTransactionId());
            }
            record.setCustomerId(req.getCustomerId());
            record.setAmount(req.getAmount());
            record.setTaxRate(req.getTaxRate());
            record.setReportedTax(req.getReportedTax());
            record.setCreatedAt(LocalDateTime.now());
            record.setRawPayloadJson(JsonUtil.toJson(req));

            // Validate required fields
            if (req.getTransactionId() == null || req.getTransactionId().trim().isEmpty()) {
                failureReasons.add("transactionId is required");
            }

            if (req.getDate() == null || req.getDate().trim().isEmpty()) {
                failureReasons.add("date is required");
            }

            if (req.getCustomerId() == null || req.getCustomerId().trim().isEmpty()) {
                failureReasons.add("customerId is required");
            }

            if (req.getAmount() == null) {
                failureReasons.add("amount is required");
            } else if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                failureReasons.add("amount must be greater than 0");
            }

            if (req.getTaxRate() == null) {
                failureReasons.add("taxRate is required");
            }

            if (req.getTransactionType() == null || req.getTransactionType().trim().isEmpty()) {
                failureReasons.add("transactionType is required");
            }

            // Parse date
            LocalDate parsedDate = null;
            if (req.getDate() != null && !req.getDate().trim().isEmpty()) {
                try {
                    parsedDate = LocalDate.parse(req.getDate());
                    record.setDate(parsedDate);
                } catch (DateTimeParseException e) {
                    failureReasons.add("invalid date format, expected yyyy-MM-dd");
                }
            }

            // Parse transaction type
            if (req.getTransactionType() != null && !req.getTransactionType().trim().isEmpty()) {
                try {
                    record.setTransactionType(TransactionType.valueOf(req.getTransactionType().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    failureReasons.add("invalid transactionType. Allowed values: SALE, REFUND, EXPENSE");
                }
            }

            // If validation failed
            if (!failureReasons.isEmpty()) {
                record.setValidationStatus(ValidationStatus.FAILURE);
                record.setFailureReasonsJson(JsonUtil.toJson(failureReasons));

                // save only if transactionId is present
                if (req.getTransactionId() != null && !req.getTransactionId().trim().isEmpty()) {
                    transactionRecordRepository.save(record);
                }

                result.setValidationStatus("FAILURE");
                result.setFailureReasons(failureReasons);
                result.setComplianceStatus(null);

                failureCount++;
                results.add(result);
                continue;
            }

            // Tax computation
            BigDecimal expectedTax = taxComputationService.calculateExpectedTax(req.getAmount(), req.getTaxRate());
            BigDecimal taxGap = taxComputationService.calculateTaxGap(expectedTax, req.getReportedTax());
            ComplianceStatus complianceStatus =
                    taxComputationService.determineCompliance(taxGap, req.getReportedTax());

            record.setExpectedTax(expectedTax);
            record.setTaxGap(taxGap);
            record.setComplianceStatus(complianceStatus);
            record.setValidationStatus(ValidationStatus.SUCCESS);
            record.setFailureReasonsJson(null);

            transactionRecordRepository.save(record);
            ruleEngineService.applyRules(record);

            auditLogService.log(
                    EventType.INGESTION,
                    req.getTransactionId(),
                    "Transaction uploaded successfully"
            );

            auditLogService.log(
                    EventType.TAX_COMPUTATION,
                    req.getTransactionId(),
                    "expectedTax=" + expectedTax + ", taxGap=" + taxGap + ", complianceStatus=" + complianceStatus
            );

            result.setValidationStatus("SUCCESS");
            result.setFailureReasons(new ArrayList<>());
            result.setComplianceStatus(complianceStatus.name());

            successCount++;
            results.add(result);
        }
        // Return summary response
        return TransactionUploadResponse.builder()
                .totalCount(requests.size())
                .successCount(successCount)
                .failureCount(failureCount)
                .results(results)
                .build();
    }
}