package com.financetracker.controller;

import com.financetracker.dto.BudgetDto;
import com.financetracker.model.User;
import com.financetracker.service.AuthService;
import com.financetracker.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired private BudgetService budgetService;
    @Autowired private AuthService authService;

    @PostMapping
    public ResponseEntity<?> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BudgetDto.Request req) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(budgetService.create(user, req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody BudgetDto.Request req) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(budgetService.update(user, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        budgetService.delete(user, id);
        return ResponseEntity.ok(Map.of("message", "Budget deleted"));
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "false") boolean currentOnly) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        if (currentOnly) {
            return ResponseEntity.ok(budgetService.getCurrentBudgets(user));
        }
        return ResponseEntity.ok(budgetService.getAllBudgets(user));
    }
}
