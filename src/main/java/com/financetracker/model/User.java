package com.financetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(precision = 15, scale = 2)
    private BigDecimal monthlyIncome = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal targetExpenses = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal savingsTarget = BigDecimal.ZERO;

    private String currency = "USD";

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Budget> budgets = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingsGoal> savingsGoals = new ArrayList<>();

    public enum Role { USER, ADMIN }

    public User() {}

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public BigDecimal getTargetExpenses() { return targetExpenses; }
    public BigDecimal getSavingsTarget() { return savingsTarget; }
    public String getCurrency() { return currency; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Transaction> getTransactions() { return transactions; }
    public List<Budget> getBudgets() { return budgets; }
    public List<SavingsGoal> getSavingsGoals() { return savingsGoals; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
    public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public void setTargetExpenses(BigDecimal targetExpenses) { this.targetExpenses = targetExpenses; }
    public void setSavingsTarget(BigDecimal savingsTarget) { this.savingsTarget = savingsTarget; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final User u = new User();
        public Builder name(String v) { u.name = v; return this; }
        public Builder email(String v) { u.email = v; return this; }
        public Builder password(String v) { u.password = v; return this; }
        public Builder role(Role v) { u.role = v; return this; }
        public Builder monthlyIncome(BigDecimal v) { u.monthlyIncome = v; return this; }
        public Builder targetExpenses(BigDecimal v) { u.targetExpenses = v; return this; }
        public Builder savingsTarget(BigDecimal v) { u.savingsTarget = v; return this; }
        public Builder currency(String v) { u.currency = v; return this; }
        public User build() { return u; }
    }
}
