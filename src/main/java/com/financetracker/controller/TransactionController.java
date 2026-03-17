package com.financetracker.controller;

import com.financetracker.dto.TransactionDto;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.service.AuthService;
import com.financetracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired private TransactionService transactionService;
    @Autowired private AuthService authService;

    @PostMapping
    public ResponseEntity<?> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionDto.Request req) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        Transaction t = transactionService.create(user, req);
        return ResponseEntity.ok(TransactionDto.Response.from(t));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TransactionDto.Request req) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        Transaction t = transactionService.update(user, id, req);
        return ResponseEntity.ok(TransactionDto.Response.from(t));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        transactionService.delete(user, id);
        return ResponseEntity.ok(Map.of("message", "Transaction deleted"));
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionService.getByDateRange(user, startDate, endDate);
        } else {
            transactions = transactionService.getAll(user);
        }
        return ResponseEntity.ok(transactions.stream()
                .map(TransactionDto.Response::from)
                .collect(Collectors.toList()));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        LocalDateTime now = LocalDateTime.now();
        int y = year == 0 ? now.getYear() : year;
        int m = month == 0 ? now.getMonthValue() : month;
        return ResponseEntity.ok(transactionService.getMonthlySummary(user, y, m));
    }

    @GetMapping("/trends")
    public ResponseEntity<?> getTrends(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "EXPENSE") String type) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        Transaction.TransactionType transType = Transaction.TransactionType.valueOf(type.toUpperCase());
        return ResponseEntity.ok(transactionService.getMonthlyTrend(user, transType));
    }
}
