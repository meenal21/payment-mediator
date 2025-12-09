package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
@SpringBootApplication
@EnableScheduling
public class PaymentMediatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentMediatorApplication.class, args);
    }
}
