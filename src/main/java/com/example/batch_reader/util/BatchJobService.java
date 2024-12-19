package com.example.batch_reader.util;

import com.example.batch_reader.model.Transaction;
import com.example.batch_reader.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class BatchJobService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> readTransactionsFromInputStream(InputStreamReader inputStreamReader) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("ACCOUNT_NUMBER")) {
                    String[] fields = line.split("\\|");
                    Transaction transaction = new Transaction(
                            null, // ID will be auto-generated by MongoDB
                            fields[0],
                            Double.parseDouble(fields[1]),
                            fields[2],
                            fields[3],
                            fields[4],
                            fields[5]
                    );
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }

    public void saveTransactions(List<Transaction> transactions) {
        transactionRepository.saveAll(transactions);
    }

    public Page<Transaction> getPaginatedTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public Page<Transaction> searchTransactionsByKeyword(String keyword, Pageable pageable) {
        return transactionRepository.findByKeyword(keyword, pageable);
    }
}