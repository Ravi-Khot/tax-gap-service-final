package com.avega.taxgap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CustomerExceptionCountDto {
    private String customerId;
    private long count;
}
