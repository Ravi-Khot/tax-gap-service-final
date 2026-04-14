package com.avega.taxgap.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.avega.taxgap.entity.ExceptionRecord;
import com.avega.taxgap.entity.RuleDefinition;
import com.avega.taxgap.entity.TransactionRecord;
import com.avega.taxgap.enums.RuleType;
import com.avega.taxgap.enums.Severity;
import com.avega.taxgap.enums.TransactionType;
import com.avega.taxgap.enums.ValidationStatus;
import com.avega.taxgap.repository.ExceptionRecordRepository;
import com.avega.taxgap.repository.RuleDefinitionRepository;
import com.avega.taxgap.repository.TransactionRecordRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RuleEngineServiceTest {

    @Mock
    private RuleDefinitionRepository ruleRepository;

    @Mock
    private ExceptionRecordRepository exceptionRepository;

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RuleEngineService ruleEngineService;

    private RuleDefinition highValueRule;
    private RuleDefinition gstRule;
    private RuleDefinition refundRule;

    @BeforeEach
    void setUp() {
        highValueRule = RuleDefinition.builder()
                .id(1L)
                .ruleName("High Value Transaction Rule")
                .ruleType(RuleType.HIGH_VALUE_TRANSACTION)
                .severity(Severity.HIGH)
                .enabled(true)
                .configJson("{\"threshold\":5000}")
                .description("High value transaction rule")
                .build();

        gstRule = RuleDefinition.builder()
                .id(2L)
                .ruleName("GST Slab Violation Rule")
                .ruleType(RuleType.GST_SLAB_VIOLATION)
                .severity(Severity.HIGH)
                .enabled(true)
                .configJson("{\"slabThreshold\":3000,\"requiredTaxRate\":0.18}")
                .description("GST slab rule")
                .build();

        refundRule = RuleDefinition.builder()
                .id(3L)
                .ruleName("Refund Validation Rule")
                .ruleType(RuleType.REFUND_VALIDATION)
                .severity(Severity.MEDIUM)
                .enabled(true)
                .configJson("{\"note\":\"Refund should not exceed sale\"}")
                .description("Refund rule")
                .build();
    }

    @Test
    void testHighValueRuleCreatesException() throws Exception {
        TransactionRecord record = TransactionRecord.builder()
                .transactionId("TXN1001")
                .customerId("CUST101")
                .amount(new BigDecimal("6000"))
                .taxRate(new BigDecimal("0.18"))
                .reportedTax(new BigDecimal("1080"))
                .transactionType(TransactionType.SALE)
                .validationStatus(ValidationStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(ruleRepository.findAll()).thenReturn(List.of(highValueRule));
        when(objectMapper.readValue(any(String.class), any(TypeReference.class)))
                .thenReturn(Map.of("threshold", 5000));

        ruleEngineService.applyRules(record);

        verify(exceptionRepository, times(1)).save(any(ExceptionRecord.class));
    }

    @Test
    void testGstRuleCreatesException() throws Exception {
        TransactionRecord record = TransactionRecord.builder()
                .transactionId("TXN1002")
                .customerId("CUST102")
                .amount(new BigDecimal("4000"))
                .taxRate(new BigDecimal("0.12"))
                .reportedTax(new BigDecimal("480"))
                .transactionType(TransactionType.SALE)
                .validationStatus(ValidationStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(ruleRepository.findAll()).thenReturn(List.of(gstRule));
        when(objectMapper.readValue(any(String.class), any(TypeReference.class)))
                .thenReturn(Map.of("slabThreshold", 3000, "requiredTaxRate", 0.18));

        ruleEngineService.applyRules(record);

        verify(exceptionRepository, times(1)).save(any(ExceptionRecord.class));
    }

    @Test
    void testRefundRuleCreatesException() throws Exception {
        TransactionRecord refundRecord = TransactionRecord.builder()
                .transactionId("TXN1003")
                .customerId("CUST103")
                .amount(new BigDecimal("7000"))
                .taxRate(new BigDecimal("0.18"))
                .reportedTax(new BigDecimal("1260"))
                .transactionType(TransactionType.REFUND)
                .validationStatus(ValidationStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        TransactionRecord saleRecord = TransactionRecord.builder()
                .transactionId("TXN1004")
                .customerId("CUST103")
                .amount(new BigDecimal("5000"))
                .transactionType(TransactionType.SALE)
                .validationStatus(ValidationStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(ruleRepository.findAll()).thenReturn(List.of(refundRule));
        when(transactionRecordRepository.findByCustomerId("CUST103"))
                .thenReturn(List.of(saleRecord, refundRecord));

        ruleEngineService.applyRules(refundRecord);

        verify(exceptionRepository, times(1)).save(any(ExceptionRecord.class));
    }

    @Test
    void testNoExceptionWhenRuleDisabled() {
        RuleDefinition disabledRule = RuleDefinition.builder()
                .id(4L)
                .ruleName("Disabled Rule")
                .ruleType(RuleType.HIGH_VALUE_TRANSACTION)
                .severity(Severity.HIGH)
                .enabled(false)
                .configJson("{\"threshold\":5000}")
                .description("Disabled rule")
                .build();

        TransactionRecord record = TransactionRecord.builder()
                .transactionId("TXN1005")
                .customerId("CUST105")
                .amount(new BigDecimal("10000"))
                .transactionType(TransactionType.SALE)
                .validationStatus(ValidationStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(ruleRepository.findAll()).thenReturn(List.of(disabledRule));

        ruleEngineService.applyRules(record);

        verify(exceptionRepository, times(0)).save(any(ExceptionRecord.class));
    }
}