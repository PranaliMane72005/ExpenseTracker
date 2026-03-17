package com.financetracker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transaction.Category category;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Budget() {}

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Transaction.Category getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setCategory(Transaction.Category category) { this.category = category; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Budget b = new Budget();
        public Builder user(User v) { b.user = v; return this; }
        public Builder category(Transaction.Category v) { b.category = v; return this; }
        public Builder amount(BigDecimal v) { b.amount = v; return this; }
        public Builder startDate(LocalDate v) { b.startDate = v; return this; }
        public Builder endDate(LocalDate v) { b.endDate = v; return this; }
        public Budget build() { return b; }
    }
}
