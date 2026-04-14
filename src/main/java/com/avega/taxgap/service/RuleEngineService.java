package com.avega.taxgap.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avega.taxgap.entity.ExceptionRecord;
import com.avega.taxgap.entity.RuleDefinition;
import com.avega.taxgap.entity.TransactionRecord;
import com.avega.taxgap.enums.TransactionType;
import com.avega.taxgap.enums.ValidationStatus;
import com.avega.taxgap.repository.ExceptionRecordRepository;
import com.avega.taxgap.repository.RuleDefinitionRepository;
import com.avega.taxgap.repository.TransactionRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class RuleEngineService {

    @Autowired
    private RuleDefinitionRepository ruleRepository;

    @Autowired
    private ExceptionRecordRepository exceptionRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Apply all enabled rules for a transaction
    public void applyRules(TransactionRecord record) {
        List<RuleDefinition> rules = ruleRepository.findAll();

        for (RuleDefinition rule : rules) {
            if (!rule.getEnabled()) {
                continue;
            }

            switch (rule.getRuleType()) {
                case HIGH_VALUE_TRANSACTION:
                    applyHighValueRule(record, rule);
                    break;

                case REFUND_VALIDATION:
                    applyRefundRule(record, rule);
                    break;

                case GST_SLAB_VIOLATION:
                    applyGstRule(record, rule);
                    break;
            }
        }
    }

    // Check if transaction amount exceeds configured threshold
    private void applyHighValueRule(TransactionRecord record, RuleDefinition rule) {
        try {
            Map<String, Object> config = parseConfig(rule.getConfigJson());
            BigDecimal threshold = new BigDecimal(config.get("threshold").toString());

            if (record.getAmount() != null && record.getAmount().compareTo(threshold) > 0) {
                saveException(record, rule, "High value transaction detected");
            }
        } catch (Exception e) {
            saveException(record, rule, "Rule config error in High Value Transaction Rule");
        }
    }

    // Validate refund amount against total sale amount of the customer
    private void applyRefundRule(TransactionRecord record, RuleDefinition rule) {
        try {
            if (record.getTransactionType() != TransactionType.REFUND) {
                return;
            }

            if (record.getAmount() == null) {
                return;
            }

            BigDecimal refundAmount = record.getAmount();

            List<TransactionRecord> customerTransactions =
                    transactionRecordRepository.findByCustomerId(record.getCustomerId());

            BigDecimal totalSaleAmount = BigDecimal.ZERO;

            for (TransactionRecord txn : customerTransactions) {
                if (txn.getTransactionType() == TransactionType.SALE
                        && txn.getValidationStatus() == ValidationStatus.SUCCESS
                        && txn.getAmount() != null) {

                    totalSaleAmount = totalSaleAmount.add(txn.getAmount());
                }
            }

            if (refundAmount.compareTo(totalSaleAmount) > 0) {
                saveException(record, rule, "Refund amount exceeds original sale amount");
            }

        } catch (Exception e) {
            saveException(record, rule, "Rule execution error in Refund Validation Rule");
        }
    }
    
    // Check GST tax rate based on slab threshold
    private void applyGstRule(TransactionRecord record, RuleDefinition rule) {
        try {
            Map<String, Object> config = parseConfig(rule.getConfigJson());

            BigDecimal slabThreshold = new BigDecimal(config.get("slabThreshold").toString());
            BigDecimal requiredTaxRate = new BigDecimal(config.get("requiredTaxRate").toString());

            if (record.getAmount() != null
                    && record.getTaxRate() != null
                    && record.getAmount().compareTo(slabThreshold) > 0
                    && record.getTaxRate().compareTo(requiredTaxRate) < 0) {

                saveException(record, rule, "GST slab violation detected");
            }
        } catch (Exception e) {
            saveException(record, rule, "Rule config error in GST Slab Violation Rule");
        }
    }

    // Convert rule config JSON into map
    private Map<String, Object> parseConfig(String configJson) throws Exception {
        return objectMapper.readValue(configJson, new TypeReference<Map<String, Object>>() {});
    }

    // Save generated exception record
    private void saveException(TransactionRecord record, RuleDefinition rule, String message) {
        ExceptionRecord ex = ExceptionRecord.builder()
                .transactionId(record.getTransactionId())
                .customerId(record.getCustomerId())
                .ruleName(rule.getRuleName())
                .severity(rule.getSeverity())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        exceptionRepository.save(ex);
    }
}