package com.financetracker.dto;

import com.financetracker.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class AuthDto {

    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;
        @Email @NotBlank private String email;
        @NotBlank @Size(min = 6) private String password;
        private BigDecimal monthlyIncome;
        private BigDecimal targetExpenses;
        private BigDecimal savingsTarget;
        private String currency = "USD";

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public BigDecimal getMonthlyIncome() { return monthlyIncome; }
        public BigDecimal getTargetExpenses() { return targetExpenses; }
        public BigDecimal getSavingsTarget() { return savingsTarget; }
        public String getCurrency() { return currency; }
        public void setName(String v) { this.name = v; }
        public void setEmail(String v) { this.email = v; }
        public void setPassword(String v) { this.password = v; }
        public void setMonthlyIncome(BigDecimal v) { this.monthlyIncome = v; }
        public void setTargetExpenses(BigDecimal v) { this.targetExpenses = v; }
        public void setSavingsTarget(BigDecimal v) { this.savingsTarget = v; }
        public void setCurrency(String v) { this.currency = v; }
    }

    public static class LoginRequest {
        @Email @NotBlank private String email;
        @NotBlank private String password;
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public void setEmail(String v) { this.email = v; }
        public void setPassword(String v) { this.password = v; }
    }

    public static class AuthResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String name;
        private String email;
        private User.Role role;
        private String currency;

        public AuthResponse(String token, Long id, String name, String email, User.Role role, String currency) {
            this.token = token; this.id = id; this.name = name;
            this.email = email; this.role = role; this.currency = currency;
        }
        public String getToken() { return token; }
        public String getType() { return type; }
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public User.Role getRole() { return role; }
        public String getCurrency() { return currency; }
    }

    public static class UpdateProfileRequest {
        private String name;
        private BigDecimal monthlyIncome;
        private BigDecimal targetExpenses;
        private BigDecimal savingsTarget;
        private String currency;
        public String getName() { return name; }
        public BigDecimal getMonthlyIncome() { return monthlyIncome; }
        public BigDecimal getTargetExpenses() { return targetExpenses; }
        public BigDecimal getSavingsTarget() { return savingsTarget; }
        public String getCurrency() { return currency; }
        public void setName(String v) { this.name = v; }
        public void setMonthlyIncome(BigDecimal v) { this.monthlyIncome = v; }
        public void setTargetExpenses(BigDecimal v) { this.targetExpenses = v; }
        public void setSavingsTarget(BigDecimal v) { this.savingsTarget = v; }
        public void setCurrency(String v) { this.currency = v; }
    }
}
