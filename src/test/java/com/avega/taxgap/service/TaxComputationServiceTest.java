package com.avega.taxgap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avega.taxgap.enums.ComplianceStatus;

public class TaxComputationServiceTest {

    private TaxComputationService taxComputationService;

    @BeforeEach
    void setUp() {
        taxComputationService = new TaxComputationService();
    }

    @Test
    void testCalculateExpectedTax() {
        BigDecimal amount = new BigDecimal("1000");
        BigDecimal taxRate = new BigDecimal("0.18");

        BigDecimal result = taxComputationService.calculateExpectedTax(amount, taxRate);

        assertEquals(new BigDecimal("180.00"), result);
    }

    @Test
    void testDetermineCompliance_Compliant() {
        BigDecimal taxGap = new BigDecimal("0.50");
        BigDecimal reportedTax = new BigDecimal("180");

        ComplianceStatus result = taxComputationService.determineCompliance(taxGap, reportedTax);

        assertEquals(ComplianceStatus.COMPLIANT, result);
    }

    @Test
    void testDetermineCompliance_Underpaid() {
        BigDecimal taxGap = new BigDecimal("50.00");
        BigDecimal reportedTax = new BigDecimal("130");

        ComplianceStatus result = taxComputationService.determineCompliance(taxGap, reportedTax);

        assertEquals(ComplianceStatus.UNDERPAID, result);
    }

    @Test
    void testDetermineCompliance_Overpaid() {
        BigDecimal taxGap = new BigDecimal("-20.00");
        BigDecimal reportedTax = new BigDecimal("200");

        ComplianceStatus result = taxComputationService.determineCompliance(taxGap, reportedTax);

        assertEquals(ComplianceStatus.OVERPAID, result);
    }

    @Test
    void testDetermineCompliance_NonCompliant_WhenReportedTaxMissing() {
        BigDecimal taxGap = new BigDecimal("100.00");

        ComplianceStatus result = taxComputationService.determineCompliance(taxGap, null);

        assertEquals(ComplianceStatus.NON_COMPLIANT, result);
    }
}
