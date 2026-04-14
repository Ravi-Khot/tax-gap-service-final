package com.avega.taxgap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ExceptionSummaryDto {

    private String ruleName;
    private long count;
}
