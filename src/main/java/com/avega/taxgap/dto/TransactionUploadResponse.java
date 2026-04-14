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
public class TransactionUploadResponse {

    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<TransactionResultDto> results;
}
