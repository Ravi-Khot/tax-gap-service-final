package com.avega.taxgap.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avega.taxgap.dto.TransactionUploadRequest;
import com.avega.taxgap.dto.TransactionUploadResponse;
import com.avega.taxgap.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // API to upload and process batch of transactions
    @PostMapping("/upload")
    public ResponseEntity<TransactionUploadResponse> uploadTransactions(
            @RequestBody List<TransactionUploadRequest> requests) {

        TransactionUploadResponse response = transactionService.processTransactions(requests);
        return ResponseEntity.ok(response);
    }
}
