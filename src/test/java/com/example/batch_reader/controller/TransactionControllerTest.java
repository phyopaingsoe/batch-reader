package com.example.batch_reader.controller;

import com.example.batch_reader.model.Transaction;
import com.example.batch_reader.util.BatchJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnableSpringDataWebSupport
public class TransactionControllerTest {

    @Mock
    private BatchJobService batchJobService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    public void testProcessFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "transactions.txt", MediaType.TEXT_PLAIN_VALUE, "ACCOUNT_NUMBER|TRX_AMOUNT|DESCRIPTION|TRX_DATE|TRX_TIME|CUSTOMER_ID\n8872838283|123.00|FUND TRANSFER|2019-09-12|11:11:11|222".getBytes());

        doNothing().when(batchJobService).saveTransactions(any());

        mockMvc.perform(multipart("/api/process").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Files uploaded and processed successfully"));

        verify(batchJobService, times(1)).readTransactionsFromInputStream(any());
        verify(batchJobService, times(1)).saveTransactions(any());
    }

    @Test
    public void testProcessFile_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "transactions.txt", MediaType.TEXT_PLAIN_VALUE, "".getBytes());

        mockMvc.perform(multipart("/api/process").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("File is empty"));

        verify(batchJobService, never()).readTransactionsFromInputStream(any());
        verify(batchJobService, never()).saveTransactions(any());
    }

    @Test
    public void testUpdateTransaction_Success() throws Exception {
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setTranId("6a0f8cdd-8277-472e-9520-3873f27ea22c");
        updatedTransaction.setDescription("Updated Description");
        updatedTransaction.setTrxAmount(150.0);

        when(batchJobService.updateTransaction(eq("12345"), any())).thenReturn(updatedTransaction);

        mockMvc.perform(put("/api/update/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"description\": \"Updated Description\", " +
                                "\"trxAmount\": 150.0" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.trxAmount").value(150.0));

        verify(batchJobService, times(1)).updateTransaction(eq("12345"), any());
    }
}
