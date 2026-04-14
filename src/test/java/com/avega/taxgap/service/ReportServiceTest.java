package com.avega.taxgap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.avega.taxgap.dto.CustomerTaxSummaryDto;
import com.avega.taxgap.entity.TransactionRecord;
import com.avega.taxgap.enums.ComplianceStatus;
import com.avega.taxgap.enums.TransactionType;
import com.avega.taxgap.enums.ValidationStatus;
import com.avega.taxgap.repository.ExceptionRecordRepository;
import com.avega.taxgap.repository.TransactionRecordRepository;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @Mock
    private ExceptionRecordRepository exceptionRecordRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void testGetCustomerTaxSummary() {
        TransactionRecord t1 = TransactionRecord.builder()
                .transactionId("TXN1001")
                .customerId("CUST101")
                .amount(new BigDecimal("1000"))
                .reportedTax(new BigDecimal("180"))
                .expectedTax(new BigDecimal("180"))
                .taxGap(new BigDecimal("0"))
                .transactionType(TransactionType.SALE)
                .validationStatus(ValidationStatus.SUCCESS)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .createdAt(LocalDateTime.now())
                .build();

        TransactionRecord t2 = TransactionRecord.builder()
                .transactionId("TXN1002")
                .customerId("CUST101")
                .amount(new BigDecimal("5000"))
                .reportedTax(new BigDecimal("500"))
                .expectedTax(new BigDecimal("900"))
                .taxGap(new BigDecimal("400"))
                .transactionType(TransactionType.SALE)
                .validationStatus(ValidationStatus.SUCCESS)
                .complianceStatus(ComplianceStatus.UNDERPAID)
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionRecordRepository.findByCustomerId("CUST101"))
                .thenReturn(List.of(t1, t2));

        CustomerTaxSummaryDto result = reportService.getCustomerTaxSummary("CUST101");

        assertEquals("CUST101", result.getCustomerId());
        assertEquals(new BigDecimal("6000"), result.getTotalAmount());
        assertEquals(new BigDecimal("680"), result.getTotalReportedTax());
        assertEquals(new BigDecimal("1080"), result.getTotalExpectedTax());
        assertEquals(new BigDecimal("400"), result.getTotalTaxGap());
        assertEquals(50.0, result.getComplianceScore());
    }
}