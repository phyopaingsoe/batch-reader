package com.example.batch_reader.controller;

import com.example.batch_reader.model.Transaction;
import com.example.batch_reader.util.BatchJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private BatchJobService batchJobService;

    @PostMapping("/process")
    public String processFile(@RequestParam("file") MultipartFile files) {
        try {

            if (files.isEmpty()) {
                return ("File is empty");
            }
            // Read the file input stream
            InputStreamReader reader = new InputStreamReader(files.getInputStream());
            List<Transaction> transactions = batchJobService.readTransactionsFromInputStream(reader);
            batchJobService.saveTransactions(transactions);
            return ("Files uploaded and processed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ("Error processing file: " + e.getMessage());
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(Pageable pageable) {
        Page<Transaction> transactions = batchJobService.getPaginatedTransactions(pageable);
        return ResponseEntity.ok(transactions.getContent());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Transaction>> searchTransactions(
            @RequestParam(value = "keyword") String keyword,
            Pageable pageable
    ) {
        Page<Transaction> transactions = batchJobService.searchTransactionsByKeyword(keyword, pageable);
        return ResponseEntity.ok(transactions.getContent());
    }

    @PutMapping("/update/{tranId}")
    public Transaction updateTransaction(
            @PathVariable String tranId,
            @RequestBody Transaction updatedRecord) {
        return batchJobService.updateTransaction(tranId, updatedRecord);
    }

}