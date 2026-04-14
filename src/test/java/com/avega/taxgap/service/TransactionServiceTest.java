package com.avega.taxgap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.avega.taxgap.dto.TransactionUploadRequest;
import com.avega.taxgap.dto.TransactionUploadResponse;
import com.avega.taxgap.entity.TransactionRecord;
import com.avega.taxgap.enums.ComplianceStatus;
import com.avega.taxgap.repository.TransactionRecordRepository;
import com.avega.taxgap.util.JsonUtil;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @Mock
    private TaxComputationService taxComputationService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private RuleEngineService ruleEngineService;

    @Spy
    private JsonUtil jsonUtil = new JsonUtil();

    @InjectMocks
    private TransactionService transactionService;

    private TransactionUploadRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = TransactionUploadRequest.builder()
                .transactionId("TXN9001")
                .date("2026-04-13")
                .customerId("CUST901")
                .amount(new BigDecimal("1000"))
                .taxRate(new BigDecimal("0.18"))
                .reportedTax(new BigDecimal("180"))
                .transactionType("SALE")
                .build();
    }

    @Test
    void testProcessTransactions_Success() {
        when(taxComputationService.calculateExpectedTax(
                new BigDecimal("1000"),
                new BigDecimal("0.18")))
                .thenReturn(new BigDecimal("180.00"));

        when(taxComputationService.calculateTaxGap(
                new BigDecimal("180.00"),
                new BigDecimal("180")))
                .thenReturn(new BigDecimal("0.00"));

        when(taxComputationService.determineCompliance(
                new BigDecimal("0.00"),
                new BigDecimal("180")))
                .thenReturn(ComplianceStatus.COMPLIANT);

        when(transactionRecordRepository.save(any(TransactionRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(ruleEngineService).applyRules(any(TransactionRecord.class));
        doNothing().when(auditLogService).log(any(), any(), any());

        TransactionUploadResponse response =
                transactionService.processTransactions(List.of(validRequest));

        assertEquals(1, response.getTotalCount());
        assertEquals(1, response.getSuccessCount());
        assertEquals(0, response.getFailureCount());
        assertEquals("SUCCESS", response.getResults().get(0).getValidationStatus());
        assertEquals("COMPLIANT", response.getResults().get(0).getComplianceStatus());
    }

    @Test
    void testProcessTransactions_Failure_WhenAmountInvalid() {
        TransactionUploadRequest invalidRequest = TransactionUploadRequest.builder()
                .transactionId("TXN9002")
                .date("2026-04-13")
                .customerId("CUST902")
                .amount(new BigDecimal("-100"))
                .taxRate(new BigDecimal("0.18"))
                .reportedTax(new BigDecimal("18"))
                .transactionType("SALE")
                .build();

        when(transactionRecordRepository.save(any(TransactionRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionUploadResponse response =
                transactionService.processTransactions(List.of(invalidRequest));

        assertEquals(1, response.getTotalCount());
        assertEquals(0, response.getSuccessCount());
        assertEquals(1, response.getFailureCount());
        assertEquals("FAILURE", response.getResults().get(0).getValidationStatus());
    }

    @Test
    void testProcessTransactions_Failure_WhenTransactionTypeInvalid() {
        TransactionUploadRequest invalidRequest = TransactionUploadRequest.builder()
                .transactionId("TXN9003")
                .date("2026-04-13")
                .customerId("CUST903")
                .amount(new BigDecimal("1000"))
                .taxRate(new BigDecimal("0.18"))
                .reportedTax(new BigDecimal("180"))
                .transactionType("WRONG_TYPE")
                .build();

        when(transactionRecordRepository.save(any(TransactionRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionUploadResponse response =
                transactionService.processTransactions(List.of(invalidRequest));

        assertEquals(1, response.getTotalCount());
        assertEquals(0, response.getSuccessCount());
        assertEquals(1, response.getFailureCount());
        assertEquals("FAILURE", response.getResults().get(0).getValidationStatus());
    }
}