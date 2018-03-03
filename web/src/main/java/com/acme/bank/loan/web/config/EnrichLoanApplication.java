package com.acme.bank.loan.web.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({
        "com.acme.bank.loan.web"
})
@SpringBootApplication
public class EnrichLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnrichLoanApplication.class, args);
    }
}
