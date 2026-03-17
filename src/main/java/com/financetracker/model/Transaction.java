package com.financetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private TransactionType type;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private String description;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    private String account;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    public enum TransactionType { INCOME, EXPENSE }

    public enum Category {
        SALARY, FREELANCE, INVESTMENT, BONUS, OTHER_INCOME,
        RENT, FOOD, TRANSPORT, ENTERTAINMENT, SHOPPING,
        UTILITIES, HEALTHCARE, EDUCATION, TRAVEL, INSURANCE,
        SAVINGS, OTHER_EXPENSE
    }

    public Transaction() {}

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public String getAccount() { return account; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setType(TransactionType type) { this.type = type; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCategory(Category category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setAccount(String account) { this.account = account; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Transaction t = new Transaction();
        public Builder user(User v) { t.user = v; return this; }
        public Builder type(TransactionType v) { t.type = v; return this; }
        public Builder amount(BigDecimal v) { t.amount = v; return this; }
        public Builder category(Category v) { t.category = v; return this; }
        public Builder description(String v) { t.description = v; return this; }
        public Builder date(LocalDate v) { t.date = v; return this; }
        public Builder account(String v) { t.account = v; return this; }
        public Transaction build() { return t; }
    }
}
