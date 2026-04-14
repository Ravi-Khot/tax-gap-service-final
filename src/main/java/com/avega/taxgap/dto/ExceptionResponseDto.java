package com.avega.taxgap.dto;

import java.time.LocalDateTime;

import com.avega.taxgap.enums.Severity;

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
public class ExceptionResponseDto {

    private String transactionId;
    private String customerId;
    private String ruleName;
    private Severity severity;
    private String message;
    private LocalDateTime timestamp;
}
