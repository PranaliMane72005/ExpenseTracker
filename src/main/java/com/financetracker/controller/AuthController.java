package com.financetracker.controller;

import com.financetracker.dto.AuthDto;
import com.financetracker.model.User;
import com.financetracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDto.RegisterRequest req) {
        try {
            return ResponseEntity.ok(authService.register(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDto.LoginRequest req) {
        try {
            return ResponseEntity.ok(authService.login(req));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "monthlyIncome", user.getMonthlyIncome(),
                "targetExpenses", user.getTargetExpenses(),
                "savingsTarget", user.getSavingsTarget(),
                "currency", user.getCurrency(),
                "createdAt", user.getCreatedAt()
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AuthDto.UpdateProfileRequest req) {
        User updated = authService.updateProfile(userDetails.getUsername(), req);
        return ResponseEntity.ok(Map.of(
                "id", updated.getId(),
                "name", updated.getName(),
                "email", updated.getEmail(),
                "monthlyIncome", updated.getMonthlyIncome(),
                "targetExpenses", updated.getTargetExpenses(),
                "savingsTarget", updated.getSavingsTarget(),
                "currency", updated.getCurrency()
        ));
    }
}
