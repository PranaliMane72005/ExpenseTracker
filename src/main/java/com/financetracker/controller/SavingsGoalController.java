package com.financetracker.controller;

import com.financetracker.dto.SavingsGoalDto;
import com.financetracker.model.SavingsGoal;
import com.financetracker.model.User;
import com.financetracker.service.AuthService;
import com.financetracker.service.SavingsGoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/savings-goals")
public class SavingsGoalController {

    @Autowired private SavingsGoalService savingsGoalService;
    @Autowired private AuthService authService;

    @PostMapping
    public ResponseEntity<?> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SavingsGoalDto.Request req) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        SavingsGoal goal = savingsGoalService.create(user, req);
        return ResponseEntity.ok(SavingsGoalDto.Response.from(goal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody SavingsGoalDto.Request req) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        SavingsGoal goal = savingsGoalService.update(user, id, req);
        return ResponseEntity.ok(SavingsGoalDto.Response.from(goal));
    }

    @PatchMapping("/{id}/add-amount")
    public ResponseEntity<?> addAmount(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody SavingsGoalDto.UpdateAmountRequest req) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        SavingsGoal goal = savingsGoalService.addAmount(user, id, req.getAmount());
        return ResponseEntity.ok(SavingsGoalDto.Response.from(goal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        savingsGoalService.delete(user, id);
        return ResponseEntity.ok(Map.of("message", "Savings goal deleted"));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        List<SavingsGoalDto.Response> goals = savingsGoalService.getAll(user)
                .stream().map(SavingsGoalDto.Response::from).collect(Collectors.toList());
        return ResponseEntity.ok(goals);
    }
}
