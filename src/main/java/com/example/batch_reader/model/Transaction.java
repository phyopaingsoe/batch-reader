package com.example.batch_reader.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    @Indexed
    private String tranId;
    private String accountNumber;
    private double trxAmount;
    private String description;
    private String trxDate;
    private String trxTime;
    private String customerId;

    @Version
    private Long version;
}