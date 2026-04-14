package com.avega.taxgap.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.avega.taxgap.enums.ComplianceStatus;

@Service
public class TaxComputationService {
	// Calculate expected tax = amount * tax rate
    public BigDecimal calculateExpectedTax(BigDecimal amount, BigDecimal taxRate) {
        if (amount == null || taxRate == null) return BigDecimal.ZERO;
        return amount.multiply(taxRate);
    }

    // Calculate tax gap = expected tax - reported tax
    public BigDecimal calculateTaxGap(BigDecimal expectedTax, BigDecimal reportedTax) {
        if (reportedTax == null) return expectedTax;
        return expectedTax.subtract(reportedTax);
    }

    // Determine compliance status based on tax gap
    public ComplianceStatus determineCompliance(BigDecimal taxGap, BigDecimal reportedTax) {

        if (reportedTax == null) {
            return ComplianceStatus.NON_COMPLIANT;
        }

        if (taxGap.abs().compareTo(BigDecimal.ONE) <= 0) {
            return ComplianceStatus.COMPLIANT;
        } else if (taxGap.compareTo(BigDecimal.ONE) > 0) {
            return ComplianceStatus.UNDERPAID;
        } else {
            return ComplianceStatus.OVERPAID;
        }
    }
}
