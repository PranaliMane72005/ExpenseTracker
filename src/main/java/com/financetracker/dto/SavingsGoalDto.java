package com.financetracker.dto;

import com.financetracker.model.SavingsGoal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SavingsGoalDto {

    public static class Request {
        @NotBlank private String goalName;
        @NotNull @Positive private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private LocalDate deadline;
        private String description;

        public String getGoalName() { return goalName; }
        public BigDecimal getTargetAmount() { return targetAmount; }
        public BigDecimal getCurrentAmount() { return currentAmount; }
        public LocalDate getDeadline() { return deadline; }
        public String getDescription() { return description; }
        public void setGoalName(String v) { this.goalName = v; }
        public void setTargetAmount(BigDecimal v) { this.targetAmount = v; }
        public void setCurrentAmount(BigDecimal v) { this.currentAmount = v; }
        public void setDeadline(LocalDate v) { this.deadline = v; }
        public void setDescription(String v) { this.description = v; }
    }

    public static class Response {
        private Long id;
        private String goalName;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private double progressPercentage;
        private LocalDate deadline;
        private String description;
        private SavingsGoal.GoalStatus status;
        private LocalDateTime createdAt;

        public static Response from(SavingsGoal g) {
            Response r = new Response();
            r.id = g.getId(); r.goalName = g.getGoalName();
            r.targetAmount = g.getTargetAmount(); r.currentAmount = g.getCurrentAmount();
            r.progressPercentage = g.getProgressPercentage(); r.deadline = g.getDeadline();
            r.description = g.getDescription(); r.status = g.getStatus(); r.createdAt = g.getCreatedAt();
            return r;
        }
        public Long getId() { return id; }
        public String getGoalName() { return goalName; }
        public BigDecimal getTargetAmount() { return targetAmount; }
        public BigDecimal getCurrentAmount() { return currentAmount; }
        public double getProgressPercentage() { return progressPercentage; }
        public LocalDate getDeadline() { return deadline; }
        public String getDescription() { return description; }
        public SavingsGoal.GoalStatus getStatus() { return status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    public static class UpdateAmountRequest {
        @NotNull @Positive private BigDecimal amount;
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal v) { this.amount = v; }
    }
}
