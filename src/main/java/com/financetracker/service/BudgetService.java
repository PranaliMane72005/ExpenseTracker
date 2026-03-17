package com.financetracker.service;

import com.financetracker.dto.BudgetDto;
import com.financetracker.model.Budget;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired private BudgetRepository budgetRepository;
    @Autowired private TransactionRepository transactionRepository;

    @Transactional
    public Budget create(User user, BudgetDto.Request req) {
        Budget budget = Budget.builder()
                .user(user)
                .category(req.getCategory())
                .amount(req.getAmount())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .build();
        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget update(User user, Long id, BudgetDto.Request req) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        budget.setCategory(req.getCategory());
        budget.setAmount(req.getAmount());
        budget.setStartDate(req.getStartDate());
        budget.setEndDate(req.getEndDate());
        return budgetRepository.save(budget);
    }

    @Transactional
    public void delete(User user, Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        budgetRepository.delete(budget);
    }

    public List<BudgetDto.Response> getCurrentBudgets(User user) {
        LocalDate today = LocalDate.now();
        List<Budget> budgets = budgetRepository
                .findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(user, today, today);

        return budgets.stream().map(b -> {
            BigDecimal spent = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, Transaction.TransactionType.EXPENSE, b.getStartDate(), b.getEndDate());
            // filter to category
            BigDecimal categorySpent = transactionRepository
                    .sumExpensesByCategoryAndDateBetween(user, b.getStartDate(), b.getEndDate())
                    .stream()
                    .filter(row -> row[0].toString().equals(b.getCategory().toString()))
                    .map(row -> (BigDecimal) row[1])
                    .findFirst().orElse(BigDecimal.ZERO);
            return BudgetDto.Response.from(b, categorySpent);
        }).collect(Collectors.toList());
    }

    public List<BudgetDto.Response> getAllBudgets(User user) {
        return budgetRepository.findByUser(user).stream().map(b -> {
            BigDecimal categorySpent = transactionRepository
                    .sumExpensesByCategoryAndDateBetween(user, b.getStartDate(), b.getEndDate())
                    .stream()
                    .filter(row -> row[0].toString().equals(b.getCategory().toString()))
                    .map(row -> (BigDecimal) row[1])
                    .findFirst().orElse(BigDecimal.ZERO);
            return BudgetDto.Response.from(b, categorySpent);
        }).collect(Collectors.toList());
    }
}
