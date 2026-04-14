package com.avega.taxgap.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avega.taxgap.dto.CustomerTaxSummaryDto;
import com.avega.taxgap.entity.TransactionRecord;
import com.avega.taxgap.repository.TransactionRecordRepository;

@Service
public class ReportService {

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    // Generate tax summary for a specific customer
    public CustomerTaxSummaryDto getCustomerTaxSummary(String customerId) {

        List<TransactionRecord> records =
                transactionRecordRepository.findByCustomerId(customerId);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalReportedTax = BigDecimal.ZERO;
        BigDecimal totalExpectedTax = BigDecimal.ZERO;
        BigDecimal totalTaxGap = BigDecimal.ZERO;

        int totalTransactions = records.size();
        int nonCompliantTransactions = 0;

        for (TransactionRecord record : records) {

        	// Add transaction amount
            if (record.getAmount() != null) {
                totalAmount = totalAmount.add(record.getAmount());
            }

            // Add reported tax
            if (record.getReportedTax() != null) {
                totalReportedTax = totalReportedTax.add(record.getReportedTax());
            }

            // Add expected tax
            if (record.getExpectedTax() != null) {
                totalExpectedTax = totalExpectedTax.add(record.getExpectedTax());
            }

            // Add tax gap
            if (record.getTaxGap() != null) {
                totalTaxGap = totalTaxGap.add(record.getTaxGap());
            }

            // Count non-compliant transactions
            if (record.getComplianceStatus() != null &&
                !"COMPLIANT".equals(record.getComplianceStatus().name())) {
                nonCompliantTransactions++;
            }
        }

        double complianceScore = 100.0;

     // Calculate compliance percentage
        if (totalTransactions > 0) {
            complianceScore =
                    100.0 - (((double) nonCompliantTransactions / totalTransactions) * 100.0);
        }

        return CustomerTaxSummaryDto.builder()
                .customerId(customerId)
                .totalAmount(totalAmount)
                .totalReportedTax(totalReportedTax)
                .totalExpectedTax(totalExpectedTax)
                .totalTaxGap(totalTaxGap)
                .complianceScore(
                        BigDecimal.valueOf(complianceScore)
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue()
                )
                .build();
    }
}