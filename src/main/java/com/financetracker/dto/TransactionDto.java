package com.financetracker.dto;

import com.financetracker.model.Transaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class TransactionDto {

    public static class Request {
        @NotNull private Transaction.TransactionType type;
        @NotNull @Positive private BigDecimal amount;
        @NotNull private Transaction.Category category;
        private String description;
        @NotNull private LocalDate date;
        private String account;

        public Transaction.TransactionType getType() { return type; }
        public BigDecimal getAmount() { return amount; }
        public Transaction.Category getCategory() { return category; }
        public String getDescription() { return description; }
        public LocalDate getDate() { return date; }
        public String getAccount() { return account; }
        public void setType(Transaction.TransactionType v) { this.type = v; }
        public void setAmount(BigDecimal v) { this.amount = v; }
        public void setCategory(Transaction.Category v) { this.category = v; }
        public void setDescription(String v) { this.description = v; }
        public void setDate(LocalDate v) { this.date = v; }
        public void setAccount(String v) { this.account = v; }
    }

    public static class Response {
        private Long id;
        private Transaction.TransactionType type;
        private BigDecimal amount;
        private Transaction.Category category;
        private String description;
        private LocalDate date;
        private String account;
        private LocalDateTime createdAt;

        public static Response from(Transaction t) {
            Response r = new Response();
            r.id = t.getId(); r.type = t.getType(); r.amount = t.getAmount();
            r.category = t.getCategory(); r.description = t.getDescription();
            r.date = t.getDate(); r.account = t.getAccount(); r.createdAt = t.getCreatedAt();
            return r;
        }
        public Long getId() { return id; }
        public Transaction.TransactionType getType() { return type; }
        public BigDecimal getAmount() { return amount; }
        public Transaction.Category getCategory() { return category; }
        public String getDescription() { return description; }
        public LocalDate getDate() { return date; }
        public String getAccount() { return account; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    public static class Summary {
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal netBalance;
        private Map<String, BigDecimal> expensesByCategory;

        public BigDecimal getTotalIncome() { return totalIncome; }
        public BigDecimal getTotalExpenses() { return totalExpenses; }
        public BigDecimal getNetBalance() { return netBalance; }
        public Map<String, BigDecimal> getExpensesByCategory() { return expensesByCategory; }
        public void setTotalIncome(BigDecimal v) { this.totalIncome = v; }
        public void setTotalExpenses(BigDecimal v) { this.totalExpenses = v; }
        public void setNetBalance(BigDecimal v) { this.netBalance = v; }
        public void setExpensesByCategory(Map<String, BigDecimal> v) { this.expensesByCategory = v; }
    }
}
