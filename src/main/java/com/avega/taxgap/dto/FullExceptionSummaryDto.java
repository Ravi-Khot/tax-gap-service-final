package com.avega.taxgap.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FullExceptionSummaryDto {
    private long totalExceptions;
    private List<SeverityCountDto> countBySeverity;
    private List<CustomerExceptionCountDto> customerWiseExceptionCount;
}
