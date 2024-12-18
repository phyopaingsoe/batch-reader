package com.example.batch_reader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class BatchReaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchReaderApplication.class, args);
	}

}
