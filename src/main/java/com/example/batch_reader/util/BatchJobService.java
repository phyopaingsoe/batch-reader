package com.example.batch_reader.util;

import com.example.batch_reader.model.Transaction;
import com.example.batch_reader.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BatchJobService {

    @Autowired
    private TransactionRepository transactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(BatchJobService.class);

    public List<Transaction> readTransactionsFromInputStream(InputStreamReader inputStreamReader) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("ACCOUNT_NUMBER")) {
                    String[] fields = line.split("\\|");
                    Transaction transaction = new Transaction(
                            null,
                            generateTransactionId(),
                            fields[0],
                            Double.parseDouble(fields[1]),
                            fields[2],
                            fields[3],
                            fields[4],
                            fields[5],
                            null
                    );
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }
    private String generateTransactionId() {
        return UUID.randomUUID().toString(); // Generates a unique ID
    }


    public Transaction updateTransaction(String tranId, Transaction updatedRecord) {
        int retryCount = 3;
        while (retryCount > 0) {
            try {
                Transaction existingRecord = transactionRepository.findByTranId(tranId);
                if(existingRecord == null)
                {
                    throw new RuntimeException("Transaction Not Found!");
                }
                logger.info(String.valueOf(existingRecord));
                existingRecord.setTrxAmount(updatedRecord.getTrxAmount());
                existingRecord.setDescription(updatedRecord.getDescription());
                existingRecord.setTrxDate(updatedRecord.getTrxDate());
                existingRecord.setTrxTime(updatedRecord.getTrxTime());
                existingRecord.setCustomerId(updatedRecord.getCustomerId());

                // Save updated record
                return transactionRepository.save(existingRecord);
            } catch (OptimisticLockingFailureException e) {
                retryCount--;
                if (retryCount == 0) {
                    throw new RuntimeException("Failed to update transaction after retries due to concurrent updates", e);
                }
            }
        }
        throw new RuntimeException("Unexpected error in updateTransaction");
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