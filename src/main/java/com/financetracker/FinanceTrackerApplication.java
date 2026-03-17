package com.financetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
public class FinanceTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinanceTrackerApplication.class, args);
        System.out.println("\n🚀 Personal Finance Tracker is running!");
        System.out.println("📊 Dashboard: http://localhost:8080");
        System.out.println("🗄️  H2 Console:  http://localhost:8080/h2-console\n");
    }
}
