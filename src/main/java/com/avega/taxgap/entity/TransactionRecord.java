package com.avega.taxgap.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.avega.taxgap.enums.ComplianceStatus;
import com.avega.taxgap.enums.TransactionType;
import com.avega.taxgap.enums.ValidationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transaction_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "txn_date")
    private LocalDate date;

    @Column(name = "customer_id")
    private String customerId;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "tax_rate", precision = 10, scale = 4)
    private BigDecimal taxRate;

    @Column(name = "reported_tax", precision = 19, scale = 2)
    private BigDecimal reportedTax;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status")
    private ValidationStatus validationStatus;

    @Column(name = "failure_reasons_json", columnDefinition = "TEXT")
    private String failureReasonsJson;

    @Column(name = "expected_tax", precision = 19, scale = 2)
    private BigDecimal expectedTax;

    @Column(name = "tax_gap", precision = 19, scale = 2)
    private BigDecimal taxGap;

    @Enumerated(EnumType.STRING)
    @Column(name = "compliance_status")
    private ComplianceStatus complianceStatus;

    @Column(name = "raw_payload_json", columnDefinition = "TEXT")
    private String rawPayloadJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
