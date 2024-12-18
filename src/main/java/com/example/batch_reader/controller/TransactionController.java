package com.example.batch_reader.controller;

import com.example.batch_reader.model.Transaction;
import com.example.batch_reader.util.BatchJobService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile[] files) {
        System.out.println("SSSSS");
        try {
            if (files.length == 0) {
                return ResponseEntity.badRequest().body("No files uploaded");
            }

            // Process each file
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    return ResponseEntity.badRequest().body("One or more files are empty");
                }

                // Read the file input stream
                InputStreamReader reader = new InputStreamReader(file.getInputStream());
                List<Transaction> transactions = batchJobService.readTransactionsFromInputStream(reader);
                batchJobService.saveTransactions(transactions);
            }

            return ResponseEntity.ok("Files uploaded and processed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }

    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return batchJobService.retrieveAllTransactions();
    }

    @GetMapping("/hello")
    public String list() {
        return "Hello";
    }
}