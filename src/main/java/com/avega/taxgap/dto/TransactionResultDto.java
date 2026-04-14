package com.avega.taxgap.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResultDto {

    private String transactionId;
    private String validationStatus;
    private List<String> failureReasons;
    private String complianceStatus;
}