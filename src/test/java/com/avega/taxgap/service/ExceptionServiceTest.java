package com.avega.taxgap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.avega.taxgap.dto.ExceptionResponseDto;
import com.avega.taxgap.entity.ExceptionRecord;
import com.avega.taxgap.enums.Severity;
import com.avega.taxgap.repository.ExceptionRecordRepository;

@ExtendWith(MockitoExtension.class)
public class ExceptionServiceTest {

    @Mock
    private ExceptionRecordRepository exceptionRecordRepository;

    @InjectMocks
    private ExceptionService exceptionService;

    @Test
    void testGetAllExceptions() {
    	// Mock data
        ExceptionRecord record1 = ExceptionRecord.builder()
                .id(1L)
                .transactionId("TXN1001")
                .customerId("CUST101")
                .ruleName("High Value Transaction Rule")
                .severity(Severity.HIGH)
                .message("High value transaction detected")
                .timestamp(LocalDateTime.now())
                .build();

        ExceptionRecord record2 = ExceptionRecord.builder()
                .id(2L)
                .transactionId("TXN1002")
                .customerId("CUST102")
                .ruleName("GST Slab Violation Rule")
                .severity(Severity.HIGH)
                .message("GST slab violation detected")
                .timestamp(LocalDateTime.now())
                .build();
        // Mock repository response
        when(exceptionRecordRepository.findAll()).thenReturn(List.of(record1, record2));

        // Call service method
        List<ExceptionResponseDto> result = exceptionService.getExceptions(null, null, null);

        // Assertions
        assertEquals(2, result.size());
        assertEquals("TXN1001", result.get(0).getTransactionId());
        assertEquals("CUST101", result.get(0).getCustomerId());
        assertEquals("High Value Transaction Rule", result.get(0).getRuleName());
        assertEquals("HIGH", String.valueOf(result.get(0).getSeverity()));
    }

    @Test
    void testGetExceptionsByCustomerId() {
        ExceptionRecord record = ExceptionRecord.builder()
                .id(1L)
                .transactionId("TXN1001")
                .customerId("CUST101")
                .ruleName("High Value Transaction Rule")
                .severity(Severity.HIGH)
                .message("High value transaction detected")
                .timestamp(LocalDateTime.now())
                .build();

        when(exceptionRecordRepository.findByCustomerId("CUST101"))
                .thenReturn(List.of(record));

        List<ExceptionResponseDto> result =
                exceptionService.getExceptions("CUST101", null, null);

        assertEquals(1, result.size());
        assertEquals("CUST101", result.get(0).getCustomerId());
        assertEquals("HIGH", String.valueOf(result.get(0).getSeverity()));
    }

    @Test
    void testGetExceptionsBySeverity() {
        ExceptionRecord record = ExceptionRecord.builder()
                .id(2L)
                .transactionId("TXN1002")
                .customerId("CUST102")
                .ruleName("GST Slab Violation Rule")
                .severity(Severity.HIGH)
                .message("GST slab violation detected")
                .timestamp(LocalDateTime.now())
                .build();

        when(exceptionRecordRepository.findBySeverity(Severity.HIGH))
                .thenReturn(List.of(record));

        List<ExceptionResponseDto> result =
                exceptionService.getExceptions(null, "HIGH", null);

        assertEquals(1, result.size());
        assertEquals("TXN1002", result.get(0).getTransactionId());
        assertEquals("HIGH", String.valueOf(result.get(0).getSeverity()));
    }

    @Test
    void testGetExceptionsByRuleName() {
        ExceptionRecord record = ExceptionRecord.builder()
                .id(3L)
                .transactionId("TXN1003")
                .customerId("CUST103")
                .ruleName("Refund Validation Rule")
                .severity(Severity.MEDIUM)
                .message("Refund amount exceeds original sale amount")
                .timestamp(LocalDateTime.now())
                .build();

        when(exceptionRecordRepository.findByRuleName("Refund Validation Rule"))
                .thenReturn(List.of(record));

        List<ExceptionResponseDto> result =
                exceptionService.getExceptions(null, null, "Refund Validation Rule");

        assertEquals(1, result.size());
        assertEquals("Refund Validation Rule", result.get(0).getRuleName());
        assertEquals("MEDIUM", String.valueOf(result.get(0).getSeverity()));
    }

    @Test
    void testGetExceptionsByCustomerIdAndSeverity() {
        ExceptionRecord record = ExceptionRecord.builder()
                .id(4L)
                .transactionId("TXN1004")
                .customerId("CUST104")
                .ruleName("High Value Transaction Rule")
                .severity(Severity.HIGH)
                .message("High value transaction detected")
                .timestamp(LocalDateTime.now())
                .build();

        when(exceptionRecordRepository.findByCustomerIdAndSeverity("CUST104", Severity.HIGH))
                .thenReturn(List.of(record));

        List<ExceptionResponseDto> result =
                exceptionService.getExceptions("CUST104", "HIGH", null);

        assertEquals(1, result.size());
        assertEquals("CUST104", result.get(0).getCustomerId());
        assertEquals("HIGH", String.valueOf(result.get(0).getSeverity()));
    }

    @Test
    void testGetExceptionsByCustomerIdAndRuleName() {
        ExceptionRecord record = ExceptionRecord.builder()
                .id(5L)
                .transactionId("TXN1005")
                .customerId("CUST105")
                .ruleName("Refund Validation Rule")
                .severity(Severity.MEDIUM)
                .message("Refund amount exceeds original sale amount")
                .timestamp(LocalDateTime.now())
                .build();

        when(exceptionRecordRepository.findByCustomerIdAndRuleName("CUST105", "Refund Validation Rule"))
                .thenReturn(List.of(record));

        List<ExceptionResponseDto> result =
                exceptionService.getExceptions("CUST105", null, "Refund Validation Rule");

        assertEquals(1, result.size());
        assertEquals("CUST105", result.get(0).getCustomerId());
        assertEquals("Refund Validation Rule", result.get(0).getRuleName());
        assertEquals("MEDIUM", String.valueOf(result.get(0).getSeverity()));
    }
}