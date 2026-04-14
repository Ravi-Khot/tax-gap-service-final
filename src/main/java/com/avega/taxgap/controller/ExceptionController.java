package com.avega.taxgap.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avega.taxgap.dto.ExceptionResponseDto;
import com.avega.taxgap.dto.ExceptionSummaryDto;
import com.avega.taxgap.service.ExceptionService;

@RestController
@RequestMapping("/api/exceptions")
public class ExceptionController {

    @Autowired
    private ExceptionService exceptionService;

    // API to fetch exception records with optional filters
    @GetMapping
    public List<ExceptionResponseDto> getExceptions(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String ruleName) {

        return exceptionService.getExceptions(customerId, severity, ruleName);
    }
    
    // API to get summary of exceptions (grouped data)
    @GetMapping("/summary")
    public List<ExceptionSummaryDto> getSummary() {
        return exceptionService.getExceptionSummary();
    }
    
    
    // API to get detailed exception summary
    @GetMapping("/full-summary")
    public com.avega.taxgap.dto.FullExceptionSummaryDto getFullSummary() {
        return exceptionService.getFullExceptionSummary();
    }
}
