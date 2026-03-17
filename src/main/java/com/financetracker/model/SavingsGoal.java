package com.financetracker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "savings_goals")
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String goalName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal targetAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    private LocalDate deadline;
    private String description;

    @Enumerated(EnumType.STRING)
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum GoalStatus { IN_PROGRESS, COMPLETED, PAUSED }

    public SavingsGoal() {}

    public double getProgressPercentage() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) return 0;
        return currentAmount.divide(targetAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getGoalName() { return goalName; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public LocalDate getDeadline() { return deadline; }
    public String getDescription() { return description; }
    public GoalStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setGoalName(String goalName) { this.goalName = goalName; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(GoalStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final SavingsGoal g = new SavingsGoal();
        public Builder user(User v) { g.user = v; return this; }
        public Builder goalName(String v) { g.goalName = v; return this; }
        public Builder targetAmount(BigDecimal v) { g.targetAmount = v; return this; }
        public Builder currentAmount(BigDecimal v) { g.currentAmount = v; return this; }
        public Builder deadline(LocalDate v) { g.deadline = v; return this; }
        public Builder description(String v) { g.description = v; return this; }
        public Builder status(GoalStatus v) { g.status = v; return this; }
        public SavingsGoal build() { return g; }
    }
}
