package com.financetracker.dto;

import com.financetracker.model.Budget;
import com.financetracker.model.Transaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BudgetDto {

    public static class Request {
        @NotNull private Transaction.Category category;
        @NotNull @Positive private BigDecimal amount;
        @NotNull private LocalDate startDate;
        @NotNull private LocalDate endDate;

        public Transaction.Category getCategory() { return category; }
        public BigDecimal getAmount() { return amount; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setCategory(Transaction.Category v) { this.category = v; }
        public void setAmount(BigDecimal v) { this.amount = v; }
        public void setStartDate(LocalDate v) { this.startDate = v; }
        public void setEndDate(LocalDate v) { this.endDate = v; }
    }

    public static class Response {
        private Long id;
        private Transaction.Category category;
        private BigDecimal budgetAmount;
        private BigDecimal spentAmount;
        private BigDecimal remainingAmount;
        private double progressPercentage;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDateTime createdAt;

        public static Response from(Budget b, BigDecimal spent) {
            Response r = new Response();
            r.id = b.getId();
            r.category = b.getCategory();
            r.budgetAmount = b.getAmount();
            r.spentAmount = spent;
            r.remainingAmount = b.getAmount().subtract(spent);
            r.progressPercentage = b.getAmount().compareTo(BigDecimal.ZERO) == 0 ? 0 :
                spent.divide(b.getAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            r.startDate = b.getStartDate();
            r.endDate = b.getEndDate();
            r.createdAt = b.getCreatedAt();
            return r;
        }
        public Long getId() { return id; }
        public Transaction.Category getCategory() { return category; }
        public BigDecimal getBudgetAmount() { return budgetAmount; }
        public BigDecimal getSpentAmount() { return spentAmount; }
        public BigDecimal getRemainingAmount() { return remainingAmount; }
        public double getProgressPercentage() { return progressPercentage; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
}
