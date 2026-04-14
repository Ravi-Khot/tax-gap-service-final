package com.avega.taxgap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avega.taxgap.dto.CustomerTaxSummaryDto;
import com.avega.taxgap.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // API to fetch tax summary report for a specific customer
    @GetMapping("/customer-summary/{customerId}")
    public CustomerTaxSummaryDto getCustomerSummary(@PathVariable String customerId) {
        return reportService.getCustomerTaxSummary(customerId);
    }
}
