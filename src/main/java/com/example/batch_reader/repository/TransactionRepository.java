package com.example.batch_reader.repository;

import com.example.batch_reader.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Page<Transaction> findAll(Pageable pageable);

    @Query("{ $or: [ " +
            "{ 'customerId': { $regex: ?0, $options: 'i' } }, " +
            "{ 'accountNumber': { $regex: ?0, $options: 'i' } }, " +
            "{ 'description': { $regex: ?0, $options: 'i' } } " +
            "] }")
    Page<Transaction> findByKeyword(String keyword, Pageable pageable);
}